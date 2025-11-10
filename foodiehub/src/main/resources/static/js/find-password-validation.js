let resetToken = '';

document.getElementById('emailForm').addEventListener('submit', async (e) => {
	e.preventDefault();

	const email = document.getElementById('email').value.trim();
	const resultMessage = document.getElementById('emailResultMessage');

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
		const response = await fetch('/user/find-password', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			},
			body: `email=${encodeURIComponent(email)}`
		});

		const data = await response.json();

		if (data.success) {
			resetToken = data.token;
			resultMessage.textContent = '인증코드가 이메일로 전송되었습니다.';
			resultMessage.className = 'result-message success';
			resultMessage.style.display = 'block';

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

document.getElementById('verifyForm').addEventListener('submit', async (e) => {
	e.preventDefault();

	const authCode = document.getElementById('authCode').value.trim();
	const resultMessage = document.getElementById('verifyResultMessage');

	if (!authCode || authCode.length !== 6) {
		resultMessage.textContent = '인증코드는 6자리입니다.';
		resultMessage.className = 'result-message error';
		resultMessage.style.display = 'block';
		return;
	}

	if (authCode === '000000') {
		resultMessage.textContent = '인증이 완료되었습니다.';
		resultMessage.className = 'result-message success';
		resultMessage.style.display = 'block';

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

document.getElementById('resetForm').addEventListener('submit', async (e) => {
	e.preventDefault();

	const newPassword = document.getElementById('newPassword').value;
	const confirmPassword = document.getElementById('confirmPassword').value;
	const resultMessage = document.getElementById('resetResultMessage');

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
		const response = await fetch('/user/reset-password', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			},
			body: `token=${encodeURIComponent(resetToken)}&newPassword=${encodeURIComponent(newPassword)}`
		});

		const data = await response.json();

		if (data.success) {
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

document.getElementById('resendBtn').addEventListener('click', (e) => {
	e.preventDefault();
	alert('인증코드를 다시 전송했습니다.');
});
