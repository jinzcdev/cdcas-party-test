package cn.edu.cdcas.partyschool.controller;

import cn.edu.cdcas.partyschool.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    private ServletContext servletContext;

    @RequestMapping("/SpringMVC")
    @ResponseBody
    public String SpringMVC(Model model) {
        model.addAttribute("msg", "Spring MVC can work");
        return "test Spring MVC can work";
    }

    @RequestMapping("/query")
    @ResponseBody
    public String query(HttpServletRequest request) {
        request.getSession().setAttribute("name", "zhangsan");
        return "set session in redis was success!!";
    }
    //可以做单地点登陆但必须配合拦截器才可以实现
    @RequestMapping("/addSession")
    @ResponseBody
    public Map testLogin(testUser user, HttpSession session) {
        //将session放入到map中
        Map userMap = (Map) servletContext.getAttribute("userMap");
        if (userMap == null) {
            userMap = new HashMap<String, HttpSession>();
            servletContext.setAttribute("userMap", userMap);
        }
        String key = user.getAccount();
        Boolean falge = userMap.containsKey(key);
        if (falge) {
            HttpSession httpSession = (HttpSession) userMap.get(user.getAccount());
            testUser user1 = (testUser) httpSession.getAttribute("user");
            session.setAttribute("user",user1);
            userMap.put(user1.getAccount(), session);
            httpSession.invalidate();
        } else {//第一次登陆了,先复制session再销毁????????发现session无法复制复制，故将session中的值set到新的session中再invalidate
            session.setAttribute("user", user);
        }
        userMap.put(user.account, session);
        return userMap;
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public Object getUser(HttpSession session) {
        return session.getAttribute("user");
    }
}

class testUser {
    String account;
    String pwd;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}