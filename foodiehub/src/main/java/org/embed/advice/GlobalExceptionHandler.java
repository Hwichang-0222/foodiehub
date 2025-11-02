package org.embed.advice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	// 에러 로그 파일 저장
	private void saveErrorLog(Exception ex, HttpServletRequest request, String errorCode) {
		try {
			String logDir = System.getProperty("user.dir") + "/logs/error";
			Path logPath = Paths.get(logDir);
			if (!Files.exists(logPath)) {
				Files.createDirectories(logPath);
			}

			String fileName = "error_" + LocalDateTime.now().format(
					DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
			Path filePath = Paths.get(logDir, fileName);

			String logContent = String.format(
					"[%s] ERROR CODE: %s\nURL: %s\nMESSAGE: %s\nDETAIL: %s\n%s\n-----------------------------------\n",
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
					errorCode,
					request.getRequestURI(),
					ex.getMessage(),
					getStackTrace(ex),
					getRequestInfo(request)
			);

			if (Files.exists(filePath)) {
				String existing = new String(Files.readAllBytes(filePath));
				Files.write(filePath, (existing + logContent).getBytes());
			} else {
				Files.write(filePath, logContent.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getStackTrace(Exception ex) {
		StringBuilder sb = new StringBuilder("STACK TRACE:\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (StackTraceElement element : elements) {
			sb.append("\tat ").append(element.toString()).append("\n");
		}
		return sb.toString();
	}

	private String getRequestInfo(HttpServletRequest request) {
		return String.format("METHOD: %s\nIP: %s\nUSER_AGENT: %s",
				request.getMethod(),
				request.getRemoteAddr(),
				request.getHeader("User-Agent"));
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "500");
		ModelAndView mav = new ModelAndView("error/error");
		mav.addObject("errorCode", "500");
		mav.addObject("errorMessage", "서버 오류가 발생했습니다");
		mav.addObject("errorDetail", "관리자에게 문의해주세요");
		return mav;
	}

	@ExceptionHandler(RuntimeException.class)
	public ModelAndView handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "500");
		ModelAndView mav = new ModelAndView("error/error");
		mav.addObject("errorCode", "500");
		mav.addObject("errorMessage", "요청 처리 중 오류가 발생했습니다");
		mav.addObject("errorDetail", "잠시 후 다시 시도해주세요");
		return mav;
	}

	@ExceptionHandler(NullPointerException.class)
	public ModelAndView handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "400");
		ModelAndView mav = new ModelAndView("error/error");
		mav.addObject("errorCode", "400");
		mav.addObject("errorMessage", "잘못된 요청입니다");
		mav.addObject("errorDetail", "필수 데이터가 누락되었습니다");
		return mav;
	}

	@ExceptionHandler(NumberFormatException.class)
	public ModelAndView handleNumberFormatException(NumberFormatException ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "400");
		ModelAndView mav = new ModelAndView("error/error");
		mav.addObject("errorCode", "400");
		mav.addObject("errorMessage", "잘못된 입력 형식입니다");
		mav.addObject("errorDetail", "숫자만 입력 가능합니다");
		return mav;
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "400");
		ModelAndView mav = new ModelAndView("error/error");
		mav.addObject("errorCode", "400");
		mav.addObject("errorMessage", "잘못된 값입니다");
		mav.addObject("errorDetail", ex.getMessage() != null ? ex.getMessage() : "입력값을 다시 확인해주세요");
		return mav;
	}

	@ExceptionHandler(Throwable.class)
	public ModelAndView handleThrowable(Throwable ex, HttpServletRequest request) {
		saveErrorLog((Exception) ex, request, "500");
		ModelAndView mav = new ModelAndView("error/error");
		mav.addObject("errorCode", "500");
		mav.addObject("errorMessage", "예상치 못한 오류가 발생했습니다");
		mav.addObject("errorDetail", "관리자에게 문의해주세요");
		return mav;
	}
}