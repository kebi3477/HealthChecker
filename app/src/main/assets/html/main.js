
let state = 'ready'; // ready, checking, done
function rand(start, end)
{
    return Math.floor((Math.random() * (end-start+1)) + start);
}
$('.count').text('0회');
$('.error').html('비정상적<br>입력 감지');

updateOtp();
function updateOtp () {
	if(state != 'ready') return;
	$('.number-view').text('OTP: ' + `${parseInt(Math.random() * 10)}${parseInt(Math.random() * 10)}${parseInt(Math.random() * 10)}${parseInt(Math.random() * 10)}${parseInt(Math.random() * 10)}${parseInt(Math.random() * 10)}`);
	setTimeout(updateOtp, 10000);
}

let nameList = ['박유천', '최종철', '정준혁', '김나리', 
				'이창수', '고동민', '김민철', '김지훈',
			   	'송창근', '김정철', '박상준', '이민호'];
let errorNameList = []; 
let errorName;

// come member
comeMember();
function comeMember() {
	let come = false;
	$('.row').each((i, v) => {
		if(come) return;
		$('.member' ,v).each((i, v) => {
			if(come) return;
			if($(v).css('display') == 'none') {
				let index = Math.floor(Math.random()*nameList.length);
				$(v).css('display', 'block');
				$('.name', v).text(nameList[index]);
				errorNameList.push(nameList[index]);
				nameList.splice(index, 1);
				come = true;
			}
		})
	})
	
	if(come && state == 'ready') {
		setTimeout(comeMember, rand(300, 800));
	}
}



// start btn event
$('.start-btn').on('click', ()=>{
	
	if(state == 'ready') {
		let a = confirm('검정을 시작하시겠습니까?');
		if(!a)
			return;
		$('.start-btn').addClass('disabled').text('측정 중');
		state = 'checking';
		updateTime();s
		errorName = errorNameList[Math.floor(Math.random()*errorNameList.length)];
		setTimeout(pushUp, rand(1000, 3000));
		setTimeout(pushUp, rand(1000, 3000));
		setTimeout(pushUp, rand(1000, 3000));
	}	
	else if(state == 'done') {
		let a = confirm('기록을 서버에 전송하시겠습니까?\n전송 후, 자동 리로드됩니다.');
		if(a)
			location.reload();
	}
})

// timer
let m = 0, s = 0;
function updateTime() {
	s++;
	if(s >= 60) {
		s = 0; 
		m++;
	}
	
	$('.number-view').text(`${m < 10 ? `0${m}` : m}:${s < 10 ? `0${s}` : s}`);
	
	if(m < 2)
		setTimeout(updateTime, 1000);
	else {
		$('.start-btn').text('완료').removeClass('disabled').css('display', 'block');
		state = 'done';
	}
}

function pushUp() {
	$('.row').each((i, v) => {
		$('.member' ,v).each((i, v) => {
			if(parseInt(Math.random() * 1000) < 500) {
				let c = parseInt($('.count', v).text().replace('회', ''))
				c++;
				$('.count', v).text(`${c}회`);
			}
			
			if($('.name', v).text() == errorName) {
				let c = parseInt($('.count', v).text().replace('회', ''))
				c += rand(1,3);
				$('.count', v).text(`${c}회`);
				if(c > 33) {
					$(v).css('background', '#ff7777');
					$('.error', v).css('color', 'white');
				}
			}
		})
	})
	
	if(state == 'checking') {
		setTimeout(pushUp, rand(1300, 3000));
	}
}
