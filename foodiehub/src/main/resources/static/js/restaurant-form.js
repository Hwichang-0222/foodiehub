// ========== 식당 추가/수정 페이지 통합 JavaScript ==========

// [1] 지도 및 Kakao 서비스 전역 변수
let map, marker, ps, geocoder;
let isEditMode = false; // 수정 모드 여부

// [2] 페이지 로드 시 초기화
window.addEventListener("DOMContentLoaded", function () {
	initializeMap();
	setupImagePreview();
	detectPageMode();
});

// [3] 지도 초기화
function initializeMap() {
	// 기존 좌표 가져오기 (edit.html에서 사용)
	const latitudeInput = document.getElementById("latitude");
	const longitudeInput = document.getElementById("longitude");
	const initialLat = latitudeInput ? parseFloat(latitudeInput.value) || 37.5665 : 37.5665;
	const initialLng = longitudeInput ? parseFloat(longitudeInput.value) || 126.9780 : 126.9780;

	const container = document.getElementById("map");
	const options = {
		center: new kakao.maps.LatLng(initialLat, initialLng),
		level: 3
	};
	map = new kakao.maps.Map(container, options);
	marker = new kakao.maps.Marker({ position: map.getCenter(), map: map });
	ps = new kakao.maps.services.Places();
	geocoder = new kakao.maps.services.Geocoder();
}

// [4] 추가/수정 모드 감지
function detectPageMode() {
	// 주소 검색 버튼의 ID로 모드 판별 (add.html: placeInput, edit.html: addressSearch)
	isEditMode = !!document.getElementById("addressSearch");
}

// [5] 식당 추가 페이지 - 장소 검색 (keywordSearch 사용)
function searchPlace() {
	const query = document.getElementById("placeInput").value.trim();
	if (!query) return alert("검색어를 입력하세요.");

	// Kakao Places API - 키워드 검색
	ps.keywordSearch(query, function (data, status) {
		if (status !== kakao.maps.services.Status.OK) {
			alert("검색 결과가 없습니다.");
			return;
		}

		// 검색 결과 목록 표시
		const resultList = document.getElementById("resultList");
		resultList.innerHTML = "";
		resultList.style.display = "block";

		data.forEach((place) => {
			const li = document.createElement("li");
			li.innerHTML = `<b>${place.place_name}</b> - ${place.address_name}`;
			li.className = "result-item";
			li.addEventListener("click", () => selectPlace(place));
			resultList.appendChild(li);
		});
	});
}

// [6] 식당 추가 페이지 - 장소 선택 처리
function selectPlace(place) {
	// 주소 필드 자동 입력
	const address = place.road_address_name || place.address_name;
	document.getElementById("placeInput").value = place.place_name;
	document.getElementById("address").value = address;
	document.getElementById("latitude").value = place.y;
	document.getElementById("longitude").value = place.x;

	// 지도 이동 및 마커 위치 업데이트
	const pos = new kakao.maps.LatLng(place.y, place.x);
	map.setCenter(pos);
	marker.setPosition(pos);

	// 좌표로 지역 자동 설정
	setRegionFromCoords(place.y, place.x);
	document.getElementById("resultList").style.display = "none";
}

// [7] 식당 수정 페이지 - 주소 검색 (addressSearch 사용)
function searchAddress() {
	const query = document.getElementById("addressSearch").value.trim();
	if (!query) return alert("주소를 입력하세요.");

	// Kakao Geocoder API - 주소 검색
	geocoder.addressSearch(query, function(result, status) {
		if (status !== kakao.maps.services.Status.OK) {
			alert("검색 결과가 없습니다. 정확한 주소를 입력해주세요.");
			return;
		}

		// 검색 결과 목록 표시
		const resultList = document.getElementById("resultList");
		resultList.innerHTML = "";
		resultList.style.display = "block";

		result.forEach((place) => {
			const li = document.createElement("li");
			li.innerHTML = `<b>${place.address_name}</b>`;
			if (place.road_address) {
				li.innerHTML += ` <small>(도로명: ${place.road_address.address_name})</small>`;
			}
			li.className = "result-item";
			li.addEventListener("click", () => selectAddress(place));
			resultList.appendChild(li);
		});
	});
}

// [8] 식당 수정 페이지 - 주소 선택 처리
function selectAddress(place) {
	// 주소 필드 자동 입력 (도로명 주소 우선)
	const address = place.road_address ? place.road_address.address_name : place.address_name;
	
	document.getElementById("address").value = address;
	document.getElementById("latitude").value = place.y;
	document.getElementById("longitude").value = place.x;

	// 지도 이동 및 마커 위치 업데이트
	const pos = new kakao.maps.LatLng(place.y, place.x);
	map.setCenter(pos);
	marker.setPosition(pos);

	document.getElementById("resultList").style.display = "none";
}

// [9] 좌표로 지역 자동 설정 (추가 페이지에서만 사용)
function setRegionFromCoords(lat, lng) {
	// 지역 선택 요소가 없으면 실행 안 함 (수정 페이지는 지역 readonly)
	const regionSelect = document.getElementById("regionSelect");
	if (!regionSelect) return;

	geocoder.coord2RegionCode(lng, lat, function (result, status) {
		if (status === kakao.maps.services.Status.OK && result.length > 0) {
			// 행정 구역 코드 타입으로 지역명 추출
			const regionInfo = result.find((r) => r.region_type === "H") || result[0];
			let area = regionInfo.region_1depth_name.trim();

			// 지역명 정규화 (전체명 → 축약명)
			const regionMap = {
				'서울특별시': '서울',
				'부산광역시': '부산',
				'대구광역시': '대구',
				'인천광역시': '인천',
				'광주광역시': '광주',
				'대전광역시': '대전',
				'울산광역시': '울산',
				'세종특별자치시': '세종',
				'경기도': '경기',
				'강원특별자치도': '강원',
				'충청북도': '충북',
				'충청남도': '충남',
				'전북특별자치도': '전북',
				'전라남도': '전남',
				'경상북도': '경북',
				'경상남도': '경남',
				'제주특별자치도': '제주'
			};

			// 축약명으로 select 값 설정
			const shortRegion = regionMap[area] || '기타';
			regionSelect.value = shortRegion;
		}
	});
}

// [10] 이미지 미리보기 설정
function setupImagePreview() {
	const mainImageInput = document.getElementById("mainImage");
	if (!mainImageInput) return;

	mainImageInput.addEventListener("change", function (e) {
		const file = e.target.files[0];
		const preview = document.getElementById("preview");
		
		if (file) {
			// FileReader로 선택된 이미지 읽기
			const reader = new FileReader();
			reader.onload = (ev) => {
				preview.src = ev.target.result;
				preview.style.display = "block";
			};
			reader.readAsDataURL(file);
		} else {
			// 파일 미선택 시 초기 상태로 복구
			preview.src = "";
			preview.style.display = "none";
		}
	});
}