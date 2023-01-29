function loginApi(data) {
    return $axios({
        'url': '/user/login',
        'method': 'post',
        data
    })
}

function sendMsg(data) {//发送手机验证码
    return $axios({
        'url': '/user/sendMsg',
        'method': 'post',
        data
    })
}

function sendMail(data) {   // 发送邮箱验证码
    return $axios({
        'url': '/user/sendMail',
        'method': 'post',
        data
    })
}

function loginoutApi() {
    return $axios({
        'url': '/user/loginout',
        'method': 'post',
    })
}

  