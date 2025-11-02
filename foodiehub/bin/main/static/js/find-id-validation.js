// 전역 변수: 재설정 토큰
let resetToken = '';

// ====== Step 1: 이메일 폼 제출 ======
document.getElementById('emailForm').addEventListener('submit', async function(e) {
	e.preventDefault();

	const email = document.getElementById('email').value.trim();
	const resultMessage = document.getElementById('emailResultMessage');

	// 이메일 검증
	if (!email) {
		resultMessage.textContent = '이메일을 입력해주세요.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	if (!emailRegex.test(email)) {
		resultMessage.textContent = '올바른 이메일 형식을 입력해주세요.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	try {
		// 서버에 요청
		const response = await fetch('/user/find-password', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			},
			body: `email=${encodeURIComponent(email)}`
		});

		const data = await response.json();

		if (data.success) {
			// 성공: 토큰 저장 및 다음 단계로 이동
			resetToken = data.token;
			resultMessage.textContent = '인증코드가 이메일로 전송되었습니다.';
			resultMessage.className = 'result-message success';
			resultMessage.style.display = 'block';

			// 2초 후 인증 폼 표시
			setTimeout(() => {
				document.getElementById('emailForm').style.display = 'none';
				document.getElementById('verifyForm').style.display = 'block';
			}, 1500);
		} else {
			// 실패: 오류 메시지
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

// ====== Step 2: 인증코드 폼 제출 ======
document.getElementById('verifyForm').addEventListener('submit', async function(e) {
	e.preventDefault();

	const authCode = document.getElementById('authCode').value.trim();
	const resultMessage = document.getElementById('verifyResultMessage');

	// 인증코드 검증
	if (!authCode || authCode.length !== 6) {
		resultMessage.textContent = '인증코드는 6자리입니다.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	// 실제로는 서버에서 검증, 여기서는 임시 처리
	if (authCode === '000000') {
		resultMessage.textContent = '인증이 완료되었습니다.';
		resultMessage.className = 'result-message success';
		resultMessage.style.display = 'block';

		// 1.5초 후 비밀번호 재설정 폼 표시
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

// ====== Step 3: 비밀번호 재설정 폼 제출 ======
document.getElementById('resetForm').addEventListener('submit', async function(e) {
	e.preventDefault();

	const newPassword = document.getElementById('newPassword').value;
	const confirmPassword = document.getElementById('confirmPassword').value;
	const resultMessage = document.getElementById('resetResultMessage');

	// 비밀번호 검증
	if (!newPassword || !confirmPassword) {
		resultMessage.textContent = '비밀번호를 입력해주세요.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	if (newPassword.length < 8) {
		resultMessage.textContent = '비밀번호는 8자 이상이어야 합니다.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	if (newPassword !== confirmPassword) {
		resultMessage.textContent = '비밀번호가 일치하지 않습니다.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	try {
		// 서버에 요청
		const response = await fetch('/user/reset-password', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			},
			body: `token=${encodeURIComponent(resetToken)}&newPassword=${encodeURIComponent(newPassword)}`
		});

		const data = await response.json();

		if (data.success) {
			// 성공: 완료 메시지 표시
			document.getElementById('resetForm').style.display = 'none';
			document.getElementById('completeMessage').style.display = 'block';
		} else {
			// 실패
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

// ====== 비밀번호 보기/숨기기 토글 ======
document.querySelectorAll('.password-box .toggle-btn').forEach(btn => {
	btn.addEventListener('click', function(e) {
		e.preventDefault();
		const input = this.previousElementSibling;
		
		if (input.type === 'password') {
			input.type = 'text';
			this.textContent = '숨기기';
		} else {
			input.type = 'password';
			this.textContent = '보기';
		}
	});
});

// ====== 인증코드 다시 전송 ======
document.getElementById('resendBtn').addEventListener('click', function(e) {
	e.preventDefault();
	alert('인증코드를 다시 전송했습니다.');
	// 실제로는 서버에서 다시 전송 로직 처리
});