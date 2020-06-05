package org.demo.acti.service.service.process.impl;

import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManagerImpl;

public class DeleteTaskCmd extends NeedsActiveTaskCmd<String> {
    public DeleteTaskCmd(String taskId) {
        super(taskId);
    }

    public String execute(CommandContext commandContext, TaskEntity currentTask) {
        // 获取所需服务
        TaskEntityManagerImpl taskEntityManager = (TaskEntityManagerImpl) commandContext.getTaskEntityManager();
        // 获取当前任务的来源任务及来源节点信息
        ExecutionEntity executionEntity = currentTask.getExecution();
        //获取历史管理
        HistoryManager historyManager = commandContext.getHistoryManager();

        //通知当前活动结束(更新act_hi_actinst)
        historyManager.recordActivityEnd(executionEntity,"jumpReason");
        //通知任务节点结束(更新act_hi_taskinst)
        historyManager.recordTaskEnd(executionEntity.getId(),"jumpReason");
        // 删除当前任务,来源任务
        taskEntityManager.deleteTask(currentTask, "jumpReason", false, false);
        return executionEntity.getId();
    }

    public String getSuspendedTaskException() {
        return "挂起的任务不能跳转";
    }
}
