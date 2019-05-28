package wrx.xing.hand.of.god.web.service.impl;

import wrx.xing.hand.of.god.web.dao.HospitalDao;
import wrx.xing.hand.of.god.web.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 请填写类的描述
 *
 * @author wangruxing
 * @date 2019-05-27 16:22
 */
@Service
public class TestServiceImpl implements TestService {

	@Autowired
	private HospitalDao hospitalDao;

	@Override
	public Object getAll() {
		return hospitalDao.getAll();
	}
}
