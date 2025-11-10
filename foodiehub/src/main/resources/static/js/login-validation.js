document.addEventListener('DOMContentLoaded', () => {
	const errorMessage = document.querySelector('.error-message');

	if (errorMessage && errorMessage.textContent.trim()) {
		alert(errorMessage.textContent.trim());
	}
});
