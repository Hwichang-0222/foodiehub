/* ==========================================
   회원가입 폼 검증 및 처리
========================================== */

document.addEventListener('DOMContentLoaded', () => {
	const signupForm = document.getElementById('signupForm');
	const signupBtn = document.getElementById('signupBtn');

	const nameInput = document.getElementById('name');
	const emailInput = document.getElementById('email');
	const checkEmailBtn = document.getElementById('checkEmailBtn');
	const emailCheckResult = document.getElementById('emailCheckResult');

	const passwordInput = document.getElementById('password');
	const passwordConfirmInput = document.getElementById('passwordConfirm');
	const passwordMatchResult = document.getElementById('passwordMatchResult');
	const togglePassword = document.getElementById('togglePassword');

	const birthInput = document.getElementById('birthDate');
	const genderSelect = document.getElementById('gender');
	const phoneInput = document.getElementById('phone');
	const addressInput = document.getElementById('address');

	// 검증 상태 변수
	let emailVerified = false;
	let passwordValid = false;
	let passwordMatched = false;
	let basicFieldsValid = false;

	/* ==========================================
	   이메일 중복 확인
	========================================== */
	checkEmailBtn.addEventListener('click', async () => {
		const email = emailInput.value.trim();

		if (email === '') {
			emailCheckResult.textContent = '이메일을 입력해주세요.';
			emailCheckResult.style.color = 'red';
			emailVerified = false;
			updateSubmitButtonState();
			return;
		}

		try {
			const response = await fetch(`/user/check-email?email=${encodeURIComponent(email)}`);
			const data = await response.json();

			if (data.exists) {
				emailCheckResult.textContent = '이미 사용 중인 이메일입니다.';
				emailCheckResult.style.color = 'red';
				emailVerified = false;
			} else {
				emailCheckResult.textContent = '사용 가능한 이메일입니다.';
				emailCheckResult.style.color = 'green';
				emailVerified = true;
			}
		} catch (error) {
			console.error('이메일 중복 확인 오류:', error);
			emailCheckResult.textContent = '서버 오류가 발생했습니다.';
			emailCheckResult.style.color = 'red';
			emailVerified = false;
		}

		updateSubmitButtonState();
	});

	// 이메일 변경 시 재확인 필요 메시지
	emailInput.addEventListener('input', () => {
		emailVerified = false;
		emailCheckResult.textContent = '이메일 변경 시 다시 중복확인을 해주세요.';
		emailCheckResult.style.color = 'gray';
		updateSubmitButtonState();
	});

	/* ==========================================
	   비밀번호 검증
	========================================== */
	// 비밀번호 강도 검증 (8~20자, 영문+숫자)
	const validatePasswordStrength = (password) => {
		const regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*]{8,20}$/;
		return regex.test(password);
	};

	const checkPasswordValidity = () => {
		const password = passwordInput.value;
		const confirm = passwordConfirmInput.value;

		if (password === '') {
			passwordMatchResult.textContent = '';
			passwordValid = false;
			passwordMatched = false;
			updateSubmitButtonState();
			return;
		}

		// 비밀번호 강도 체크
		if (!validatePasswordStrength(password)) {
			passwordMatchResult.textContent = '비밀번호는 8~20자, 영문과 숫자를 포함해야 합니다.';
			passwordMatchResult.style.color = 'red';
			passwordValid = false;
		} else {
			passwordValid = true;
		}

		// 비밀번호 확인 일치 체크
		if (confirm.length > 0) {
			if (password === confirm) {
				passwordMatchResult.textContent = '비밀번호가 일치합니다.';
				passwordMatchResult.style.color = 'green';
				passwordMatched = true;
			} else {
				passwordMatchResult.textContent = '비밀번호가 일치하지 않습니다.';
				passwordMatchResult.style.color = 'red';
				passwordMatched = false;
			}
		}

		updateSubmitButtonState();
	};

	passwordInput.addEventListener('input', checkPasswordValidity);
	passwordConfirmInput.addEventListener('input', checkPasswordValidity);

	// 비밀번호 표시/숨기기 토글
	togglePassword.addEventListener('click', () => {
		const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
		passwordInput.setAttribute('type', type);
		togglePassword.textContent = type === 'password' ? '보기' : '숨기기';
	});

	/* ==========================================
	   휴대폰 번호 자동 포맷팅
	========================================== */
	phoneInput.addEventListener('input', () => {
		let value = phoneInput.value.replace(/[^0-9]/g, '');

		// 010-0000-0000 형식으로 자동 포맷팅
		if (value.length > 3 && value.length <= 7) {
			value = value.replace(/(\d{3})(\d+)/, '$1-$2');
		} else if (value.length > 7) {
			value = value.replace(/(\d{3})(\d{4})(\d+)/, '$1-$2-$3');
		}

		phoneInput.value = value;
		validateBasicFields();
	});

	/* ==========================================
	   기본 필드 검증
	========================================== */
	const validateBasicFields = () => {
		const nameValid = nameInput && nameInput.value.trim().length > 0;
		const birthValid = birthInput && birthInput.value.trim().length > 0;
		const genderValid = genderSelect && genderSelect.value.trim().length > 0;
		const phoneValid = /^010-\d{4}-\d{4}$/.test(phoneInput.value.trim());
		const addressValid = addressInput && addressInput.value.trim().length > 0;

		basicFieldsValid = nameValid && birthValid && genderValid && phoneValid && addressValid;
		updateSubmitButtonState();
	};

	// 기본 필드 입력 이벤트 리스너
	[nameInput, birthInput, genderSelect, addressInput].forEach((input) => {
		input.addEventListener('input', validateBasicFields);
		input.addEventListener('change', validateBasicFields);
	});

	/* ==========================================
	   폼 제출 검증
	========================================== */
	signupForm.addEventListener('submit', (e) => {
		// 이메일 중복확인 체크
		if (!emailVerified) {
			e.preventDefault();
			emailCheckResult.textContent = '이메일 중복확인을 완료해주세요.';
			emailCheckResult.style.color = 'red';
			return false;
		}

		// 비밀번호 형식 체크
		if (!passwordValid) {
			e.preventDefault();
			passwordMatchResult.textContent = '비밀번호 형식이 올바르지 않습니다.';
			passwordMatchResult.style.color = 'red';
			return false;
		}

		// 비밀번호 일치 체크
		if (!passwordMatched) {
			e.preventDefault();
			passwordMatchResult.textContent = '비밀번호가 일치하지 않습니다.';
			passwordMatchResult.style.color = 'red';
			return false;
		}

		// 기본 필드 체크
		if (!basicFieldsValid) {
			e.preventDefault();
			alert('모든 필드를 올바르게 입력해주세요.');
			return false;
		}
	});

	/* ==========================================
	   제출 버튼 상태 업데이트
	========================================== */
	const updateSubmitButtonState = () => {
		// 모든 검증이 통과되면 버튼 활성화
		if (emailVerified && passwordValid && passwordMatched && basicFieldsValid) {
			signupBtn.disabled = false;
		} else {
			signupBtn.disabled = true;
		}
	};
});
