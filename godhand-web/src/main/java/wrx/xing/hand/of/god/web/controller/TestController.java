package wrx.xing.hand.of.god.web.controller;

import wrx.xing.hand.of.god.web.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 请填写类的描述
 *
 * @author wangruxing
 * @date 2019-05-27 16:19
 */
@Controller
@RequestMapping("/test")
public class TestController {

	@Autowired
	private TestService testService;

	@RequestMapping("/hello")
	@ResponseBody
	public Object hello() {
		return testService.getAll();
	}

}
