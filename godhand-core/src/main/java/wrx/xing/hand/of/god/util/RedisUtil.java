package wrx.xing.hand.of.god.util;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

/**
 * 请填写类的描述
 *
 * @author wangruxing
 * @date 2019-05-28 17:12
 */
public class RedisUtil {

	/**
	 * 数据源
	 */
	private static DataSource dataSource;

	/**
	 * 是否有数据源
	 * @param servletContext Servlet容器
	 * @return 返回
	 */
	public static boolean haveRedis(ServletContext servletContext) {
		return false;
	}
}
