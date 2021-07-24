package com.gen.common.interceptors;

import com.gen.common.base.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;

import java.util.Map;

/**
 * 日志拦截，查看执行耗时
 *
 * @author admin
 */
public class LogInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		long start = System.currentTimeMillis();
		try {
			inv.invoke();
		} finally {
			long usedTime = System.currentTimeMillis() - start;
			String args = "";
			Map<String, String[]> parameterMap = (((BaseController) inv.getTarget()).getRequest()).getParameterMap();
			for (String s : parameterMap.keySet()) {
				args += s + "=";
				for (String value : parameterMap.get(s)) {
					args += value + ",";
				}
				args = args.substring(0, args.length() - 1) + ";";
			}
			if (StrKit.notBlank(args)) {
				args = args.substring(0, args.length() - 1);
			}
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("Url           : " + inv.getActionKey());
			System.out.println("Controller    : " + inv.getController());
			System.out.println("Method        : " + inv.getMethodName());
			if (StrKit.notBlank(args)) {
				System.out.println("Parameter     : " + args);
			}
			System.out.println("TimeConsuming : " + usedTime + "ms");
			System.out.println("--------------------------------------------------------------------------------");
		}
	}
}
