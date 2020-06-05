package org.demo.acti.service.service.process;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.apache.commons.lang3.StringUtils;
import org.demo.acti.service.model.process.dto.CompleteTaskDto;
import org.demo.acti.service.model.process.dto.StartProcessDto;
import org.demo.acti.service.model.process.pojo.ProcessDefEntity;
import org.demo.acti.service.model.process.pojo.TaskDetailEntity;
import org.demo.acti.service.model.process.pojo.TaskEntity;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface IProcessService {

    /**
     * 定义的流程
     *
     * @return
     */
    List<ProcessDefEntity> processDefs();

    /**
     * 开启流程
     *
     * @param dto
     * @return
     */
    TaskEntity startProcess(StartProcessDto dto);

    /**
     * 待办
     *
     * @param userId
     * @return
     */
    List<TaskEntity> agenda(String userId);

    /**
     * 任务详情
     *
     * @param taskId
     * @return
     */
    TaskDetailEntity taskDetail(String taskId);

    /**
     * 完成任务
     *
     * @param dto
     */
    void completeTask(CompleteTaskDto dto);

    /**
     * 流程图
     */
    InputStream generateDiagram(String processIntanceId);

    /**
     * 流程图
     */
    byte[] generateDiagramBytes(String processIntanceId);

    /**
     * 查询历史按开始时间排序
     */
    List<HistoricActivityInstance> porcessOrderedHistory(String processIntanceId);

    /**
     * 删除已部署流程
     * @param deploymentName
     */
    void deleteProcDeployment(String deploymentName) throws Exception;
    /**
     * 获取高亮的线路
     *
     * @param bpmnModel
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    static List<String> getHighLightedFlowIds(BpmnModel bpmnModel, ProcessDefinitionEntity processDefinitionEntity, List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlowIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(historicActivityInstances)) {
            // HistoricActivityInstance::activityId->HistoricActivityInstance
            Map<String, HistoricActivityInstance> activityIdMap = historicActivityInstances.stream()
                    .collect(Collectors.toMap(HistoricActivityInstance::getActivityId, Function.identity(), (f, s) -> s));
            //线路结束点id为key,线路为value的map
            Map<String, SequenceFlow> endSequenceFlowMap = new HashMap<>();
            for (HistoricActivityInstance inst : historicActivityInstances) {
                // 任务对应的节点
                FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(inst.getActivityId());
                //节点所有出去的线路
                List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
                for (SequenceFlow sequenceFlow : outgoingFlows) {
                    // 起点
                    FlowNode sourceFlowNode = (FlowNode) sequenceFlow.getSourceFlowElement();
                    HistoricActivityInstance sourceInst = activityIdMap.get(sourceFlowNode.getId());
                    if (StringUtils.isNotBlank(sourceInst.getDeleteReason()) //该节点不是主动完成
                            || Objects.isNull(sourceInst.getEndTime())) { //该节点没有完成
                        continue;
                    }
                    // 结束点
                    FlowNode targetFlowNode = (FlowNode) sequenceFlow.getTargetFlowElement();
                    HistoricActivityInstance targetInst = activityIdMap.get(targetFlowNode.getId());
                    if (Objects.nonNull(targetInst)) { //执行到或执行过该节点

                        // 结束事件只有一个,最晚指向结束事件节点的线路才是正确的线路
                        if ("endEvent".equals(targetInst.getActivityType())) {
                            SequenceFlow endSequenceFlow = endSequenceFlowMap.get(targetInst.getActivityId());
                            if (Objects.nonNull(endSequenceFlow)) {
                                highFlowIds.remove(endSequenceFlow.getId());
                            }
                        }
                        endSequenceFlowMap.put(targetInst.getActivityId(), sequenceFlow);
                        highFlowIds.add(sequenceFlow.getId());
                    }
                }
            }
        }
        return highFlowIds;
    }

    /**
     * 跳转至任意节点
     * @param curTaskId
     * @param targetFlowNodeId
     */
    void jump(String curTaskId, String targetFlowNodeId);

    /**
     * 退回任意节点
     * @param curTaskId
     * @param processIntanceId
     * @param targetFlowNodeId
     */
    void backTaskDealer(String curTaskId, String processIntanceId, String targetFlowNodeId);

    /**
     * 获取流程审批备注
     * @param processIntanceId
     */
    List<Map<String, Object>> historyAndComment(String processIntanceId);
}
