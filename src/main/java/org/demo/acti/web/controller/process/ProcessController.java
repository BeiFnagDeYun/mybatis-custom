package org.demo.acti.web.controller.process;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.demo.acti.service.model.process.dto.CompleteTaskDto;
import org.demo.acti.service.model.process.dto.StartProcessDto;
import org.demo.acti.service.model.process.pojo.ProcessDefEntity;
import org.demo.acti.service.model.process.pojo.TaskDetailEntity;
import org.demo.acti.service.model.process.pojo.TaskEntity;
import org.demo.acti.service.service.process.IProcessService;
import org.demo.acti.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/process")
@Api(tags = {"流程管理"})
public class ProcessController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ProcessController.class);
    @Autowired
    private IProcessService processService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;

    @PostMapping("/deploy")
    @ApiOperation("部署流程")
    public void deploy() {
        Deployment deployment = repositoryService.createDeployment()//创建一个部署对象
                .name("请假流程")
                .addClasspathResource("processes/leaveRequest.bpmn")
                .addClasspathResource("processes/leaveRequest.png")
                .deploy();
        log.info("部署ID:{}", deployment.getId());
        log.info("部署名称:{}", deployment.getName());
    }

    @PostMapping("/processDefs")
    @ApiOperation("查看所有定义的流程")
    public List<ProcessDefEntity> processDefs() {
        return processService.processDefs();
    }

    @PostMapping("/start")
    @ApiOperation("启动流程")
    public TaskEntity startLeaveProcess(@RequestBody @Valid StartProcessDto dto) {
        return processService.startProcess(dto);
    }

    @GetMapping("/agenda")
    @ApiOperation("查询个人待办")
    public List<TaskEntity> agenda(@RequestParam("userId") String userId) {
        return processService.agenda(userId);
    }

    @GetMapping("/taskDetail")
    @ApiOperation("任务详情")
    public TaskDetailEntity taskDetail(@RequestParam("taskId") String taskId) {
        return processService.taskDetail(taskId);
    }


    @GetMapping("/showImage")
    @ApiOperation("显示流程图")
    public void showImage(HttpServletResponse response,
                          @RequestParam("processIntanceId") String processIntanceId) {
        InputStream imageSteam = processService.generateDiagram(processIntanceId);
        outputProcessImage(imageSteam, response);
    }

    @PostMapping("/completeTask")
    @ApiOperation("完成任务")
    public void completeTask(@RequestBody @Valid CompleteTaskDto dto) {
        processService.completeTask(dto);
    }

    @GetMapping("/delDeploy")
    @ApiOperation("删除流程")
    public void delDeploy(@RequestParam("deploymentName") String deploymentName) throws Exception {
        processService.deleteProcDeployment(deploymentName);
    }

    @GetMapping("/history")
    @ApiOperation("历史任务")
    public List<HistoricTaskInstance> history(@RequestParam("userId") String userId) {
        return historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .list();
    }

    @GetMapping("/jump")
    @ApiOperation("跳转任意节点")
    public void history(@RequestParam("curTaskId") String curTaskId,
                                              @RequestParam("targetFlowNodeId")String targetFlowNodeId) {
        processService.jump(curTaskId, targetFlowNodeId);
    }

    @GetMapping("/backTaskDealer")
    @ApiOperation("返回指定节点")
    public void backTaskDealer(@RequestParam("curTaskId") String curTaskId,
                               @RequestParam("processIntanceId")String processIntanceId,
                               @RequestParam("targetFlowNodeId")String targetFlowNodeId) {
        processService.backTaskDealer(curTaskId, processIntanceId, targetFlowNodeId);
    }

    @GetMapping("/hisApproveList")
    @ApiOperation("获取历史审批节点")
    public List<HistoricTaskInstance> hisApproveList(@RequestParam("processIntanceId") String processIntanceId) {
        return historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processIntanceId)//使用流程实例ID查询
                .finished() // 查询已经完成的任务
                .list();
    }

    @GetMapping("/historyAndComment")
    @ApiOperation("获取流程历史审批意见")
    public void historyAndComment(@RequestParam("processIntanceId") String processIntanceId){
        processService.historyAndComment(processIntanceId);
    }

    private void outputProcessImage(InputStream imageSteam, HttpServletResponse response) {
        try (InputStream input = imageSteam; OutputStream ouput = response.getOutputStream()) {
            response.setContentType("image/svg+xml");
            int len;
            byte[] temp = new byte[512];
            while ((len = input.read(temp)) > 0) {
                ouput.write(temp, 0, len);
            }
            ouput.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setProcessService(IProcessService processService) {
        this.processService = processService;
    }
}
