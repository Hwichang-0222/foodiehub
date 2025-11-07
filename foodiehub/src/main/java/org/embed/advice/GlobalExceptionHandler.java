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

	/* ============================================
	   에러 로그 파일 저장
	============================================ */
	
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

	/* ============================================
	   Controller 레벨 예외 처리
	   - 로그 저장 후 에러 페이지로 이동
	   - 에러 메시지는 화면에 표시 안 함 (로그에만 저장)
	============================================ */

	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "500");
		return new ModelAndView("error/error");
	}

	@ExceptionHandler(RuntimeException.class)
	public ModelAndView handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "500");
		return new ModelAndView("error/error");
	}

	@ExceptionHandler(NullPointerException.class)
	public ModelAndView handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "400");
		return new ModelAndView("error/error");
	}

	@ExceptionHandler(NumberFormatException.class)
	public ModelAndView handleNumberFormatException(NumberFormatException ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "400");
		return new ModelAndView("error/error");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
		saveErrorLog(ex, request, "400");
		return new ModelAndView("error/error");
	}

	@ExceptionHandler(Throwable.class)
	public ModelAndView handleThrowable(Throwable ex, HttpServletRequest request) {
		saveErrorLog((Exception) ex, request, "500");
		return new ModelAndView("error/error");
	}
}