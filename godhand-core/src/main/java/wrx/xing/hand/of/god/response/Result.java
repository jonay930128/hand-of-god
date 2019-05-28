package wrx.xing.hand.of.god.response;

import lombok.Data;

/**
 * 请填写类的描述
 *
 * @author wangruxing
 * @date 2019-05-27 18:15
 */
@Data
public class Result {
	/**
	 * 是否成功
	 */
	private boolean success;
	/**
	 * 数据
	 */
	private Object data;
	/**
	 * 错误信息
	 */
	private String msg;

	/**
	 * 成功
	 * @param data 数据
	 * @return 返回结果
	 */
	public static Result buildSuccess(Object data) {
		Result result = new Result();
		result.setSuccess(true);
		result.setData(data);
		return result;
	}

	/**
	 * 失败
	 * @param msg 数据
	 * @return 返回结果
	 */
	public static Result buildFail(String msg) {
		Result result = new Result();
		result.setSuccess(false);
		result.setMsg(msg);
		return result;
	}
}
