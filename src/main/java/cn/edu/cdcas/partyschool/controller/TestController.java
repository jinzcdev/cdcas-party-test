package cn.edu.cdcas.partyschool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.edu.cdcas.partyschool.model.SysUserSnail;
import cn.edu.cdcas.partyschool.service.TestService;
@Controller
@RequestMapping("/test")
public class TestController {
	
	@Autowired
	private TestService testService;
	
	@RequestMapping("/SpringMVC")
	@ResponseBody
	public String SpringMVC(Model model) {
		model.addAttribute("msg","Spring MVC can work");
		return "test Spring MVC can work"; 
	}
	
	@RequestMapping("/MyBatis") 
	@ResponseBody
	public List<SysUserSnail> MyBatis() {
		List<SysUserSnail> userList = null;
		try {
			userList=testService.findAllUser();
			System.out.println("MyBatis and fastjson can work");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userList;
	}
	
}