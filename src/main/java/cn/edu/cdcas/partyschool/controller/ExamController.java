package cn.edu.cdcas.partyschool.controller;

import cn.edu.cdcas.partyschool.service.ExamService;
import cn.edu.cdcas.partyschool.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * @Author Snail
 * @Describe about exam function
 * @CreateTime 2019/1/27
 */
@RestController
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;
    /**
     *@Describe: 查找在当前时间是否存在考试
     * 
     *@Author Snail
     *@Date 2019/1/27
     */
    @RequestMapping("/haveExam")
    private boolean haveExam(){
        try {
            int rows=examService.selectState();
            if(rows==0){
                return false;
            }else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}