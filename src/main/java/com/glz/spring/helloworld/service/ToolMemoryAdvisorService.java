package com.glz.spring.helloworld.service;

import com.glz.spring.helloworld.tool.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolMemoryAdvisorService {

    @Autowired
    private ChatClient chatClient;

    public void toolMemoryAdvisorDemo(){
        String question = "今天是几号？另外，距离 2026 年 12 月 31 日还有多少天？";

        //记忆默认order = Advisor.DEFAULT_CHAT_MEMORY_PRECEDENCE_ORDER；ToolCallingAdvisor 默认更大（更靠内层）。
        int memoryOrder = Advisor.DEFAULT_CHAT_MEMORY_PRECEDENCE_ORDER;

        // ---------- 场景 A：记忆在外层（默认顺序）----------
        // 工具用默认 order（比记忆大→更内层）。记忆包住整个工具循环，工具中间消息不入记忆。
        ChatMemory memoryA = MessageWindowChatMemory.builder().build();
        Advisor toolAdvisorA = ToolCallingAdvisor.builder().build();
        Advisor memAdvisorA = MessageChatMemoryAdvisor.builder(memoryA).order(memoryOrder).build();
        runScenario("A · 记忆在【外层】（默认", question,memoryA,toolAdvisorA,memAdvisorA);

        // ---------- 场景 B：记忆在内层 ----------
        // 把工具 order 调到比记忆更小（更外层）→ 记忆落到工具循环【内部】，每轮工具调用都经过记忆，
        // 中间消息会被记进记忆。此时关闭工具 advisor 自带的历史，交由内层记忆来承载对话历史。
        ChatMemory memoryB = MessageWindowChatMemory.builder().build();
        Advisor toolAdvisorB = ToolCallingAdvisor.builder()
                .advisorOrder(memoryOrder - 100)
                .disableInternalConversationHistory()
                .build();
        Advisor memAdvisorB = MessageChatMemoryAdvisor.builder(memoryB).order(memoryOrder).build();
        runScenario("B · 记忆在【内层】", question, memoryB, toolAdvisorB, memAdvisorB);
        System.out.println("""
                ────────────────────────────────────────────────
                对比结论：
                  - 场景 A（记忆在外层，默认）：记忆里只有「用户提问 + 最终回答」，工具调用细节被挡在外面 → 历史精简。
                  - 场景 B（记忆在内层）：记忆里还多出「模型请求调用工具 / 工具返回结果」等中间消息 → 保留完整工具轨迹。
                选择依据：想要干净的对话历史就让记忆在外层（默认）；想把工具调用过程也持久化（审计/调试/可复盘）就让记忆在内层。
                """);
    }

    private void runScenario(String label, String question, ChatMemory memory, Advisor toolAdvisor, Advisor memoryAdvisor) {
        String conversationId = "conv-" + label.charAt(0);
        System.out.println("\n========== 场景 " + label + " ==========");
        System.out.println("问：" + question);


        String answer = chatClient.prompt()
                .user(question)
                .tools(new DateTimeTools())
                .advisors(toolAdvisor,memoryAdvisor)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId))
                .call()
                .content();

        System.out.println("答：" + answer);

        List<Message> stored = memory.get(conversationId);

        System.out.println("→ 这次对话结束后，记忆里实际存了 " + stored.size() + " 条消息：");
        for (Message m : stored) {
            System.out.printf("    [%s] %s%n", m.getMessageType(), describe(m));
        }
    }

    /** 把一条消息渲染成人类可读的说明：普通文本照常显示；工具调用/工具结果则展开工具名、参数、返回值。 */
    private static String describe(Message m) {
        // 助手「请求调用工具」的消息：文本通常为空，真正内容在 getToolCalls()
        if (m instanceof AssistantMessage am && !am.getToolCalls().isEmpty()){
            StringBuilder sb = new StringBuilder();
            String text = clean(am.getText());
            if(!text.isEmpty()){
                sb.append(text).append(" ");
            }
            for (AssistantMessage.ToolCall tc : am.getToolCalls()){
                sb.append("🔧 请求调用工具 ").append(tc.name())
                        .append("(").append(clean(tc.arguments())).append(")");
            }
            return sb.toString();
        }
        // 工具「返回结果」的消息：内容在 getResponses()
        if (m instanceof ToolResponseMessage tm) {
            StringBuilder sb = new StringBuilder();
            for (ToolResponseMessage.ToolResponse r : tm.getResponses()) {
                sb.append("↩ 工具 ").append(r.name())
                        .append(" 返回: ").append(truncate(clean(r.responseData()), 80));
            }
            return sb.toString();
        }
        // 其它（用户提问、助手最终回答）：显示文本
        String text = clean(m.getText());
        return text.isEmpty() ? "(空)" : truncate(text, 60);
    }


    private static String clean(String s) {
        return s == null ? "" : s.replaceAll("\\s+", " ").trim();
    }

    private static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

}
