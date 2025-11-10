/* ==========================================
   식당 추가/수정 폼 - 전역 변수
========================================== */
let map, marker, ps, geocoder;
let isEditMode = false;

/* ==========================================
   페이지 로드 시 초기화
========================================== */
window.addEventListener('DOMContentLoaded', () => {
	initializeMap();
	setupImagePreview();
	detectPageMode();
});

/* ==========================================
   Kakao 지도 초기화
========================================== */
const initializeMap = () => {
	const latitudeInput = document.getElementById('latitude');
	const longitudeInput = document.getElementById('longitude');

	// 초기 좌표 설정 (서울 시청 기본)
	const initialLat = latitudeInput ? parseFloat(latitudeInput.value) || 37.5665 : 37.5665;
	const initialLng = longitudeInput ? parseFloat(longitudeInput.value) || 126.9780 : 126.9780;

	const container = document.getElementById('map');
	const options = {
		center: new kakao.maps.LatLng(initialLat, initialLng),
		level: 3
	};

	map = new kakao.maps.Map(container, options);
	marker = new kakao.maps.Marker({ position: map.getCenter(), map: map });
	ps = new kakao.maps.services.Places();
	geocoder = new kakao.maps.services.Geocoder();
};

/* ==========================================
   페이지 모드 감지 (추가/수정)
========================================== */
const detectPageMode = () => {
	isEditMode = !!document.getElementById('addressSearch');
};

/* ==========================================
   장소 검색 (식당 추가 모드)
========================================== */
const searchPlace = () => {
	const query = document.getElementById('placeInput').value.trim();
	if (!query) return alert('검색어를 입력하세요.');

	ps.keywordSearch(query, (data, status) => {
		if (status !== kakao.maps.services.Status.OK) {
			alert('검색 결과가 없습니다.');
			return;
		}

		// 검색 결과 목록 표시
		const resultList = document.getElementById('resultList');
		resultList.innerHTML = '';
		resultList.style.display = 'block';

		data.forEach((place) => {
			const li = document.createElement('li');
			li.innerHTML = `<b>${place.place_name}</b> - ${place.address_name}`;
			li.className = 'result-item';
			li.addEventListener('click', () => selectPlace(place));
			resultList.appendChild(li);
		});
	});
};

/* ==========================================
   장소 선택 처리
========================================== */
const selectPlace = (place) => {
	const address = place.road_address_name || place.address_name;

	// 폼 필드 자동 입력
	document.getElementById('placeInput').value = place.place_name;
	document.getElementById('address').value = address;
	document.getElementById('latitude').value = place.y;
	document.getElementById('longitude').value = place.x;

	// 지도 중심 이동 및 마커 표시
	const pos = new kakao.maps.LatLng(place.y, place.x);
	map.setCenter(pos);
	marker.setPosition(pos);

	// 지역 자동 설정
	setRegionFromCoords(place.y, place.x);

	// 검색 결과 목록 숨김
	document.getElementById('resultList').style.display = 'none';
};

/* ==========================================
   주소 검색 (식당 수정 모드)
========================================== */
const searchAddress = () => {
	const query = document.getElementById('addressSearch').value.trim();
	if (!query) return alert('주소를 입력하세요.');

	geocoder.addressSearch(query, (result, status) => {
		if (status !== kakao.maps.services.Status.OK) {
			alert('검색 결과가 없습니다. 정확한 주소를 입력해주세요.');
			return;
		}

		// 검색 결과 목록 표시
		const resultList = document.getElementById('resultList');
		resultList.innerHTML = '';
		resultList.style.display = 'block';

		result.forEach((place) => {
			const li = document.createElement('li');
			li.innerHTML = `<b>${place.address_name}</b>`;
			if (place.road_address) {
				li.innerHTML += ` <small>(도로명: ${place.road_address.address_name})</small>`;
			}
			li.className = 'result-item';
			li.addEventListener('click', () => selectAddress(place));
			resultList.appendChild(li);
		});
	});
};

/* ==========================================
   주소 선택 처리 (수정 모드)
========================================== */
const selectAddress = (place) => {
	const address = place.road_address ? place.road_address.address_name : place.address_name;

	// 폼 필드 자동 입력
	document.getElementById('address').value = address;
	document.getElementById('latitude').value = place.y;
	document.getElementById('longitude').value = place.x;

	// 지도 중심 이동 및 마커 표시
	const pos = new kakao.maps.LatLng(place.y, place.x);
	map.setCenter(pos);
	marker.setPosition(pos);

	// 검색 결과 목록 숨김
	document.getElementById('resultList').style.display = 'none';
};

/* ==========================================
   좌표로부터 지역 자동 설정
========================================== */
const setRegionFromCoords = (lat, lng) => {
	const regionSelect = document.getElementById('regionSelect');
	if (!regionSelect) return;

	geocoder.coord2RegionCode(lng, lat, (result, status) => {
		if (status === kakao.maps.services.Status.OK && result.length > 0) {
			const regionInfo = result.find((r) => r.region_type === 'H') || result[0];
			let area = regionInfo.region_1depth_name.trim();

			// 지역명 매핑 (전체 이름 -> 축약형)
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

			const shortRegion = regionMap[area] || '기타';
			regionSelect.value = shortRegion;
		}
	});
};

/* ==========================================
   이미지 미리보기
========================================== */
const setupImagePreview = () => {
	const mainImageInput = document.getElementById('mainImage');
	if (!mainImageInput) return;

	mainImageInput.addEventListener('change', (e) => {
		const file = e.target.files[0];
		const preview = document.getElementById('preview');

		if (file) {
			// 파일 읽기 및 미리보기 표시
			const reader = new FileReader();
			reader.onload = (ev) => {
				preview.src = ev.target.result;
				preview.style.display = 'block';
			};
			reader.readAsDataURL(file);
		} else {
			// 파일이 없으면 미리보기 숨김
			preview.src = '';
			preview.style.display = 'none';
		}
	});
};
