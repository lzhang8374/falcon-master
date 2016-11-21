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
@RequestMapping(value = "/service")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @RequestMapping
    public String service(Model model) {
        return "service";
    }

    @RequestMapping(value = "/getservice")
    @ResponseBody
    public String getService() {
        Result result = new Result();
        result.setSuccess(true);
        result.setResult(this.serviceService.getService());
        return JsonBinder.buildNormalBinder().toJson(result);
    }
}
