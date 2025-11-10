/* ==========================================
   비밀번호 찾기 - 전역 변수
========================================== */
let resetToken = '';

/* ==========================================
   1단계: 이메일 인증 코드 전송
========================================== */
document.getElementById('emailForm').addEventListener('submit', async (e) => {
	e.preventDefault();

	const email = document.getElementById('email').value.trim();
	const resultMessage = document.getElementById('emailResultMessage');

	// 이메일 입력 확인
	if (!email) {
		resultMessage.textContent = '이메일을 입력해주세요.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	// 이메일 형식 검증
	const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	if (!emailRegex.test(email)) {
		resultMessage.textContent = '올바른 이메일 형식을 입력해주세요.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	try {
		const response = await fetch('/user/find-password', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			},
			body: `email=${encodeURIComponent(email)}`
		});

		const data = await response.json();

		if (data.success) {
			// 인증 토큰 저장
			resetToken = data.token;
			resultMessage.textContent = '인증코드가 이메일로 전송되었습니다.';
			resultMessage.className = 'result-message success';
			resultMessage.style.display = 'block';

			// 1.5초 후 다음 단계로 이동
			setTimeout(() => {
				document.getElementById('emailForm').style.display = 'none';
				document.getElementById('verifyForm').style.display = 'block';
			}, 1500);
		} else {
			resultMessage.textContent = data.message || '처리 중 오류가 발생했습니다.';
			resultMessage.className = 'result-message error';
			resultMessage.style.display = 'block';
		}
	} catch (error) {
		console.error('Error:', error);
		resultMessage.textContent = '오류가 발생했습니다. 다시 시도해주세요.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
	}
});

/* ==========================================
   2단계: 인증 코드 확인
========================================== */
document.getElementById('verifyForm').addEventListener('submit', async (e) => {
	e.preventDefault();

	const authCode = document.getElementById('authCode').value.trim();
	const resultMessage = document.getElementById('verifyResultMessage');

	// 인증 코드 길이 확인
	if (!authCode || authCode.length !== 6) {
		resultMessage.textContent = '인증코드는 6자리입니다.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	// 인증 코드 검증 (임시 코드: 000000)
	if (authCode === '000000') {
		resultMessage.textContent = '인증이 완료되었습니다.';
		resultMessage.className = 'result-message success';
		resultMessage.style.display = 'block';

		// 1.5초 후 다음 단계로 이동
		setTimeout(() => {
			document.getElementById('verifyForm').style.display = 'none';
			document.getElementById('resetForm').style.display = 'block';
		}, 1500);
	} else {
		resultMessage.textContent = '인증코드가 일치하지 않습니다.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
	}
});

/* ==========================================
   3단계: 비밀번호 재설정
========================================== */
document.getElementById('resetForm').addEventListener('submit', async (e) => {
	e.preventDefault();

	const newPassword = document.getElementById('newPassword').value;
	const confirmPassword = document.getElementById('confirmPassword').value;
	const resultMessage = document.getElementById('resetResultMessage');

	// 비밀번호 입력 확인
	if (!newPassword || !confirmPassword) {
		resultMessage.textContent = '비밀번호를 입력해주세요.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	// 비밀번호 최소 길이 확인
	if (newPassword.length < 8) {
		resultMessage.textContent = '비밀번호는 8자 이상이어야 합니다.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	// 비밀번호 일치 확인
	if (newPassword !== confirmPassword) {
		resultMessage.textContent = '비밀번호가 일치하지 않습니다.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	try {
		const response = await fetch('/user/reset-password', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			},
			body: `token=${encodeURIComponent(resetToken)}&newPassword=${encodeURIComponent(newPassword)}`
		});

		const data = await response.json();

		if (data.success) {
			// 비밀번호 변경 성공 - 완료 메시지 표시
			document.getElementById('resetForm').style.display = 'none';
			document.getElementById('completeMessage').style.display = 'block';
		} else {
			resultMessage.textContent = data.message || '처리 중 오류가 발생했습니다.';
			resultMessage.className = 'result-message error';
			resultMessage.style.display = 'block';
		}
	} catch (error) {
		console.error('Error:', error);
		resultMessage.textContent = '오류가 발생했습니다. 다시 시도해주세요.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
	}
});

/* ==========================================
   비밀번호 표시/숨기기 토글
========================================== */
document.querySelectorAll('.password-box .toggle-btn').forEach((btn) => {
	btn.addEventListener('click', (e) => {
		e.preventDefault();
		const input = btn.previousElementSibling;

		if (input.type === 'password') {
			input.type = 'text';
			btn.textContent = '숨기기';
		} else {
			input.type = 'password';
			btn.textContent = '보기';
		}
	});
});

/* ==========================================
   인증 코드 재전송
========================================== */
document.getElementById('resendBtn').addEventListener('click', (e) => {
	e.preventDefault();
	alert('인증코드를 다시 전송했습니다.');
});
