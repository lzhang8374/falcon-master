package org.trex.falcon.admin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.trex.falcon.admin.service.ServiceService;
import org.trex.falcon.admin.utils.JsonBinder;
import org.trex.falcon.admin.web.Result;

import java.util.List;

@Controller
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @RequestMapping(value = "service")
    public String service(Model model) {
        return "service";
    }

    @RequestMapping(value = "detail")
    public String detail(Model model, String service) {
        model.addAttribute("service", service);
        return "detail";
    }


    @RequestMapping(value = "/getservices")
    @ResponseBody
    public String getService() {
        Result result = new Result();
        result.setSuccess(true);
        result.setResult(this.serviceService.getServices());
        return JsonBinder.buildNormalBinder().toJson(result);
    }

    @RequestMapping(value = "/getproviders")
    @ResponseBody
    public String getProviders(String service) {
        Result result = new Result();
        result.setSuccess(true);
        result.setResult(this.serviceService.getProviders(service));
        return JsonBinder.buildNormalBinder().toJson(result);
    }

    @RequestMapping(value = "/getprovider")
    @ResponseBody
    public String getProvider(String service, String provider) {
        Result result = new Result();
        result.setSuccess(true);
        result.setResult(this.serviceService.getProvider(service, provider));
        return JsonBinder.buildNormalBinder().toJson(result);
    }

    @RequestMapping(value = "/setpriority")
    @ResponseBody
    public String setPriority(String service, String provider, String priority) {
        Result result = new Result();
        result.setSuccess(true);
        this.serviceService.setPriority(service, provider, priority);
        return JsonBinder.buildNormalBinder().toJson(result);
    }

    @RequestMapping(value = "/getconsumers")
    @ResponseBody
    public String getConsumers(String service) {
        Result result = new Result();
        result.setSuccess(true);
        result.setResult(this.serviceService.getConsumers(service));
        return JsonBinder.buildNormalBinder().toJson(result);
    }

    @RequestMapping(value = "/getchart")
    @ResponseBody
    public String getChart(String service, String provider) {
        Result result = new Result();
        result.setSuccess(true);
        result.setResult(this.serviceService.getChart(service, provider));
        return JsonBinder.buildNormalBinder().toJson(result);
    }
}
