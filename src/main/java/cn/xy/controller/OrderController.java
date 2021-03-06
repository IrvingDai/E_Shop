package cn.xy.controller;

import cn.xy.bean.OrderAndGoodList;
import cn.xy.bean.OrderList;
import cn.xy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/bought",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Integer> bought(@RequestBody(required=true) Map<String,Object> map){
        int goods_id = (int) map.get("goods_id");
        int user_id = (int) map.get("user_id");
        //System.out.println(goods_id+"---------"+user_id);
        return orderService.bought(goods_id,user_id);
    }

    @RequestMapping(value = "/modifyOrderStatus",method = RequestMethod.POST)
    @ResponseBody
    public Map<String ,Integer> modifyOrderStatus(@RequestBody(required=true) Map<String,Object> map, HttpServletRequest request){

        Map<String ,Integer> result = new HashMap<>();

        int od_id = (int) map.get("od_id");
        String status = orderService.getOrderDetailsStatusByOrderDetailsId(od_id);
        String details_status = (String) map.get("newstatus");

        if(status.equals("未付款")){
            result.put("code",-1);
            return result;
        }
        if(status.equals("已付款")&&details_status.equals("已发货")){
            orderService.modifyStatus(od_id,details_status);
            result.put("code",1);
            return result;
        }else if(status.equals("已收货")&&details_status.equals("已完成")){
            orderService.modifyStatus(od_id,details_status);
            result.put("code",1);
            return result;
        }else if(status.equals("已发货")&&details_status.equals("已收货")){
            orderService.modifyStatus(od_id,details_status);
            result.put("code",1);
            return result;
        }else{
            result.put("code",-2);
            return result;
        }
    }

    @RequestMapping(value = "/deleteDetails",method = RequestMethod.POST)
    @ResponseBody
    public void deleteDetails(@RequestBody(required=true) Map<String,Object> map, HttpServletRequest request){
        int od_id = (int) map.get("od_id");
        orderService.deleteDetails(od_id);
    }

    @RequestMapping(value = "/getOrdersByStatus",method = RequestMethod.POST)
    @ResponseBody
    public List<OrderAndGoodList> getOrdersByStatus(@RequestBody(required=true) Map<String,Object> map, HttpServletRequest request){
        HttpSession httpSession = request.getSession();
        int user_id = (int) httpSession.getAttribute("userId");
        String status = (String) map.get("status");
        List<OrderAndGoodList> orderLists = orderService.getOrderListByDetailStatus(user_id,status);
        return orderLists;
    }

    @RequestMapping(value = "/getHistoryOrders",method = RequestMethod.POST)
    @ResponseBody
    public List<OrderAndGoodList> getHistoryOrders(HttpServletRequest request){
        HttpSession httpSession = request.getSession();
        int user_id = (int) httpSession.getAttribute("userId");
        List<OrderAndGoodList> orderLists = orderService.getHistoryOrders(user_id);
        return orderLists;
    }


}
