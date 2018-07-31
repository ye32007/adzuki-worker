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

import com.adzuki.worker.domain.TaskServiceConfig;
import com.adzuki.worker.domain.TimerTaskConfig;
import com.adzuki.worker.manager.TaskServiceConfigManager;
import com.adzuki.worker.manager.TimerTaskConfigManager;
import com.adzuki.worker.util.JSONUtil;
import com.adzuki.worker.web.vo.PageData;
import com.adzuki.worker.web.vo.PageParam;
import com.adzuki.worker.web.vo.Result;
import com.adzuki.worker.web.vo.TimerTaskConfigQuery;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("timer")
public class TimerTaskConfigController {

	private static final Logger logger = LoggerFactory.getLogger(TimerTaskConfigController.class);
	
	@Autowired
	private TimerTaskConfigManager timerTaskConfigManager;
	
	@Autowired
	private TaskServiceConfigManager taskServiceConfigManager;

	@RequestMapping(value = { "list" })
    public ModelAndView list() {
    	ModelAndView mv = new ModelAndView("timer/list");
    	List<TaskServiceConfig> typeList = taskServiceConfigManager.findAll();
    	mv.addObject("typeList", typeList);
        return mv;
    }
	
	@RequestMapping("/queryPage")
	@ResponseBody
	public Result<?> query(TimerTaskConfigQuery query, PageParam pageParam) {
		logger.debug("timer query params : {}" , JSONUtil.toJson(query));
		Result<PageInfo<TimerTaskConfig>> result = timerTaskConfigManager.queryPage(query, pageParam);
		PageData<TimerTaskConfig> pageData = new PageData<TimerTaskConfig>(result.getData().getTotal(), result.getData().getList());
		return Result.createSuccessResult(pageData);
	}
	
	
	@RequestMapping("/get")
	@ResponseBody
	public Result<?> get(Integer id) {
		TimerTaskConfig config = timerTaskConfigManager.findById(id);
		return Result.createSuccessResult(config);
	}

	@RequestMapping("/save")
	@ResponseBody
	public Result<?> save(TimerTaskConfig config) {
		// TODO:校验
		if(config.getId() == null) {
			config.setCreateTime(new Date());
			config.setLastUpdate(new Date());
			timerTaskConfigManager.save(config);
		} else {
			config.setLastUpdate(new Date());
			timerTaskConfigManager.update(config);
		}
		return Result.createSuccessResult();
	}
	
	@RequestMapping("/del")
	@ResponseBody
	public Result<?> del(Integer id) {
		timerTaskConfigManager.deleteById(id);
		return Result.createSuccessResult();
	}
}
