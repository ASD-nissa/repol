package com.example.aspect;


import com.example.EntityClass.Record;
import com.example.EntityClass.User;
import com.example.service.RecordService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

@Component
@Aspect
public class LogAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Date firstTime = new Date();

    @Autowired
    private HttpSession session;

    @Autowired
    RecordService recordService;

    @Value("${my.waitTime}")
    private int waitTime;

    @Pointcut("!@annotation(com.example.annotation.NoAspect) && execution(* com.example.web.*.*(..))")
    public void log(){
        System.out.println("log");
    }

    @Before("log()")
    public void doBefor(JoinPoint joinPoint){
        System.out.println("Before");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String ClassMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        RequestLog requestlog = new RequestLog(url,ip,ClassMethod,args);
        logger.info(requestlog.toString());

        //向数据库插入访问数据
        Long count = recordService.getMaxIdCount();
        count = count==null?0L:count;
        boolean flag = true;
        if(session.isNew()){
                count++;
        }else if(recordService.getRecordsByUrlAndCount(url,count).size() != 0
                && (new Date().getTime()-firstTime.getTime())/1000 <= waitTime){
            flag = false;
        }
        if(flag) {
            User user = (User) session.getAttribute("user");
            String name;
            if (user == null) {
                name = "匿名";
            } else {
                name = user.getNickname();
            }

            Record record = new Record(count, name, url, ip, ClassMethod);
            recordService.saveRecord(record);
            firstTime = new Date();
        }
        //插入完成
        System.out.println(requestlog);
    }

    @After("log()")
    public void doAfter(){
        System.out.println("After");
    }

    @AfterReturning(returning = "res",pointcut = "log()")
    public void doAdterReturning(Object res){
        System.out.println("AfterReturning");
    }


}

class RequestLog{
    private String url;
    private String ip;
    private String ClassMethod;
    private Object[] args;

    public RequestLog(String url, String ip, String classMethod, Object[] args) {
        this.url = url;
        this.ip = ip;
        ClassMethod = classMethod;
        this.args = args;
    }

    @Override
    public String toString() {
        return "RequestLog{" +
                "url='" + url + '\'' +
                ", ip='" + ip + '\'' +
                ", ClassMethod='" + ClassMethod + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}

