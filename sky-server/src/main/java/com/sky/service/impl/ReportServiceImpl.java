package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 根据时间区间统计营业额
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        List<LocalDate>dateList=new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end))
        {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        //将日期拼接成字符串
        String join = StringUtils.join(dateList, ',');
        //得到每一天的营业额，并把它放入一个容器中
        //一天的营业额为从0点到24点的已完成的订单的总金额
        List<Double>sum=new ArrayList<>();
        for(LocalDate localDate:dateList)
        {
            LocalDateTime beginTime=LocalDateTime.of(localDate,LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(localDate,LocalTime.MAX);
            Map map=new HashMap();
            map.put("status",Orders.COMPLETED);
            map.put("begin",beginTime);
            map.put("end",endTime);
            Double turnover=orderMapper.sumByMap(map);
                 turnover=turnover==null?0.0:turnover;
                 sum.add(turnover);
        }


         return TurnoverReportVO.builder().dateList(join).turnoverList(StringUtils.join(sum,',')).build();


    }
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
    //根据起始日期，截止日期得到日期字符串，用户总量字符串，新增用户字符串

        List<LocalDate>dateList=new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end))
        {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }


        //得到每一天的用户总量字符串->创建日期大于0点小于24点
        //得到每一天的新增用户->创建日期小于当天24点
        List<Integer>usersums=new ArrayList<>();
        List<Integer>newusers=new ArrayList<>();

        for(LocalDate localDate:dateList)
        {
        LocalDateTime a=LocalDateTime.of(localDate,LocalTime.MIN);
        LocalDateTime b=LocalDateTime.of(localDate,LocalTime.MAX);


        Integer usersum=getUserCount(null,b);
            usersum=usersum==null?0:usersum;
            Integer newuser=getUserCount(a,b);
            newuser=newuser==null?0:newuser;
            newusers.add(newuser);
            usersums.add(usersum);
        }
        return  UserReportVO.builder().
                dateList( StringUtils.join(dateList,',')).
                newUserList(StringUtils.join(newusers,',')).
                totalUserList(  StringUtils.join(usersums,',')).
                build();





    }

    /**
     * 根据时间区间统计用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    private Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end", endTime);
        return userMapper.countByMap(map);
    }
    /**
     * 根据时间区间统计订单数量
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end){
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //每天订单总数集合
        List<Integer> orderCountList = new ArrayList<>();
        //每天有效订单数集合
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //查询每天的总订单数 select count(id) from orders where order_time > ? and order_time < ?
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            //查询每天的有效订单数 select count(id) from orders where order_time > ? and order_time < ? and status = ?
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //时间区间内的总订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //时间区间内的总有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

    }
    /**
     * 根据时间区间统计指定状态的订单数量
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        Map map = new HashMap();
        map.put("status", status);
        map.put("begin",beginTime);
        map.put("end", endTime);
        return orderMapper.countByMap(map);
    }
    /**
     * 查询指定时间区间内的销量排名top10
     * @param begin
     * @param end
     * @return
     * */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end){
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()),",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()),",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}