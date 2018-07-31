package com.adzuki.worker.web.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.adzuki.worker.domain.Task;
import com.adzuki.worker.domain.TaskServiceConfig;
import com.adzuki.worker.manager.TaskManager;
import com.adzuki.worker.manager.TaskServiceConfigManager;
import com.adzuki.worker.util.JSONUtil;
import com.adzuki.worker.web.vo.PageData;
import com.adzuki.worker.web.vo.PageParam;
import com.adzuki.worker.web.vo.Result;
import com.adzuki.worker.web.vo.TaskQuery;
import com.github.pagehelper.PageInfo;

/**
 */
@Controller
@RequestMapping("task")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskManager taskManager;
    @Autowired
	private TaskServiceConfigManager taskServiceConfigManager;
    
    @RequestMapping(value = { "list" })
    public ModelAndView list() {
    	ModelAndView mv = new ModelAndView("task/list");
    	List<TaskServiceConfig> typeList = taskServiceConfigManager.findAll();
    	mv.addObject("typeList", typeList);
        return mv;
    }
    
    @RequestMapping("/queryPage")
	@ResponseBody
	public Result<?> query(TaskQuery query, PageParam pageParam) {
    	logger.debug("task query params : {}" , JSONUtil.toJson(query));
		Result<PageInfo<Task>> result = taskManager.queryPage(query, pageParam);
		PageData<Task> pageData = new PageData<Task>(result.getData().getTotal(), result.getData().getList());
		return Result.createSuccessResult(pageData);
	}

	@RequestMapping("/get")
	@ResponseBody
	public Result<?> get(Integer id) {
		Task task = taskManager.findById(id);
		return Result.createSuccessResult(task);
	}

	@RequestMapping("/save")
	@ResponseBody
	public Result<?> save(Task task) {
		// TODO:校验
		if(task.getId() == null) {
			task.setCreateTime(new Date());
			task.setLastUpdate(new Date());
			taskManager.save(task);
		} else {
			task.setLastUpdate(new Date());
			taskManager.update(task);
		}
		return Result.createSuccessResult();
	}
	
	@RequestMapping("/del")
	@ResponseBody
	public Result<?> del(Integer id) {
		taskManager.deleteById(id);
		return Result.createSuccessResult();
	}
	
	@RequestMapping("/reset")
	@ResponseBody
	public Result<?> reset(Integer id) {
		taskManager.resetTaskById(Long.valueOf(id) , 0);
		return Result.createSuccessResult();
	}
	
}
