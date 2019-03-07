package cn.edu.cdcas.partyschool.service.impl;
import cn.edu.cdcas.partyschool.mapper.ExamMapper;
import cn.edu.cdcas.partyschool.mapper.UserMapper;
import cn.edu.cdcas.partyschool.model.Exam;
import cn.edu.cdcas.partyschool.model.Manger;
import cn.edu.cdcas.partyschool.model.User;
import cn.edu.cdcas.partyschool.service.UserService;
import cn.edu.cdcas.partyschool.util.JSONResult;
import cn.edu.cdcas.partyschool.util.impl.JedisClientSingle;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private JedisClientSingle jedisClient;
    @Autowired
    private ServletContext servletContext;


    @Override
    public int deleteById(Integer id) {
        return userMapper.deleteById(id);
    }

    @Override
    public int deleteByStuNo(String stuNo) {
        return userMapper.deleteByStuNo(stuNo);
    }

    @Override
    public void clear() {
        userMapper.clear();
    }

    @Override
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int insertSelective(User user) {
        return userMapper.insertSelective(user);
    }

    @Override
    public User queryById(Integer id) {
        return userMapper.queryById(id);
    }

    @Override
    public List<User> queryAll() {
        return userMapper.queryAll();
    }

    @Override
    public List<User> queryAllByPaging(int offsetSize, int pageSize) {
        return userMapper.queryAllByPaging(offsetSize, pageSize);
    }

    @Override
    public List<User> queryAllByPagingAndKey(int offsetSize, int pageSize, String field, String value) {
        return userMapper.queryAllByPagingAndKey(offsetSize, pageSize, field, value);
    }

    @Override
    public int updateByIdSelective(User user) {
        return userMapper.updateByIdSelective(user);
    }

    @Override
    public int updateByStuNoSelective(User user) {
        return userMapper.updateByStuNoSelective(user);
    }

    @Override
    public int updateByStuNo(User user) {
        return userMapper.updateByStuNo(user);
    }

    @Override
    public int queryStuNums() {
        return userMapper.queryStuNums();
    }

    @Override
    public int queryStuNumsByField(String field, String value) {
        return userMapper.queryStuNumsByField(field, value);
    }

    @Override
    public boolean isEmpty() {
        return userMapper.queryStuNums() == 0;
    }

    @Override
    /*需在登陆时session中设置httpSession.setAttribute("authority")*/
    public JSONResult MangerAuthorityControl(HttpSession httpSession) {
        String authority = (String) httpSession.getAttribute("authority");
        if ("ROOT".equals(authority)) {//是超级管理员，给管理员管理权限
            return new JSONResult(0, "", 0);
        } else {
            return new JSONResult(1, "", 0);
        }
    }

    @Override
    public int insertManger(User user) {
        return userMapper.insert(user);
    }

    @Override
    public boolean existsManager(User user) {
        return userMapper.queryManagerNums(user) > 0;
    }

    @Override
    public boolean exists(User user) throws Exception {
        return userMapper.queryByStuNo(user.getStudentNo()) != null;
    }

    @Override
    public Map<String, Object> queryMangerMap(int page, int limit) {
        Map<String, Object> map = new HashMap<>();
        List<Manger> list = userMapper.queryMangerList((page - 1) * limit, limit);
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", userMapper.queryMangerCount());
        map.put("status", 200);
        map.put("data", list);
        return map;
    }

    public Map<String, Object> dimQueryMangerByName(String name) {
        Map<String, Object> map = new HashMap<>();
        List<Manger> list = userMapper.dimQueryMangerByName(name);
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", list.size());//直接给list大小
        map.put("status", 200);
        map.put("data", list);
        return map;
    }

    @Override
    public String findType(String number) throws Exception {

        return userMapper.findType(number);
    }
    /**
     *@Describe: 根据学号判断是否有自己的考试
     *@Author Snail
     *@Date 2019/2/1
     */
    @Override
    public String determineExam(String number) throws Exception {
        String exam_state = userMapper.isHaveExamByStudentNo(number);

        if("0".equals(exam_state)) {
            return "未考";
        }else if("3".equals(exam_state)){
            return "未补考";
        }else {
            return "无考试";
        }
    }
    /**
     *@Describe: 通过学号查找个人信息
     *@Author Snail
     *@Date 2019/3/4
     */
    @Override
    public User queryByStuNo(String stuNo) throws Exception {
        return userMapper.queryByStuNo(stuNo);
    }
    /**
     *@Describe: 获取前台需要的个人数据
     *@Author Snail
     *@Date 2019/3/4
     */
    @Override
    public Map<String,Object> studentExamInfo(String studentNo) throws Exception {
        Map<String,Object> studentExamInfo=new HashedMap<>();

        User user = this.queryByStuNo(studentNo);
        studentExamInfo.put("user",user);

        Exam exam = examMapper.findExamById(String.valueOf(user.getExamId()));
        studentExamInfo.put("exam",exam);

        return studentExamInfo;
    }
    /**
     *@Describe: 判断从PHP服务器过来的用户信息是否正确，返回学号，失败返回-1
     *@Author Snail
     *@Date 2019/3/4
     */
    @Override
    public String isLoginSuccess(String token) throws Exception {
        //判断

        return token;
    }

    /**
     *@Describe: 根据考试随机抽取题目  1.获取到本次考试各个题目数量 2.随机得到4种题型对应的题目数量
     *@Author Snail
     *@Date 2019/3/5
     */
    @Override
    public List<Map<String, Object>> requiredQuestionAndOther() throws Exception {
        //获取本次考试各种题型数量
        if(jedisClient.hexists("partySys2016","examQueNum")){
            String examQueNum = jedisClient.hget("partySys2016", "examQueNum");
        }else {
            Exam exam = examMapper.queryCurrentExamInformation().get(0);
            int radioNum=exam.getRadioNum();
            int checkNum=exam.getCheckNum();
            int judgeNum=exam.getJudgeNum();
            Integer fillNum = exam.getFillNum();
            String examQueNum =radioNum+","+checkNum+","+judgeNum+","+fillNum;
            jedisClient.hset("partySys2016","examQueNum",examQueNum);
        }
        //随机获取各种类型题目id



        List<Map<String,Object>> requiredQuestionAndOther=new ArrayList<>();
//        questions.put("open_or_close",exam.getOpenOrClose());
//        map.put("is_makeup",exam.getIsMakeup());

        //设置该系统在redis中产生的partySys2016的ttl
        //设置过期时间为总考试时间+30*60   s
        if(jedisClient.ttl("partySys2016").longValue()==new Long((long)-1)){
            Exam nowExam=(Exam)servletContext.getAttribute("exam");
            //计算相差的秒
            int second= (int) ((nowExam.getExamEndTime().getTime()-nowExam.getExamStartTime().getTime())/1000);
            jedisClient.expire("partySys2016",second);
        }
        return requiredQuestionAndOther;
    }

    /**
     *@Describe: 根据当前ExamState判断本次开始考试的状态变化
     *@Author Snail
     *@Date 2019/3/6
     */
    @Override
    public int changeExamState(int examState) throws Exception {

        if(examState==0||examState==3){
            examState=examState+1;
        }
        return examState;
    }

}
