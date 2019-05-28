function ajaxRequest(url,type,async,data) {
    var result = {"success":false,"msg":"未知异常"};
    jQuery.ajax({
        url: url,
        type: type,
        async:async,
        traditional:true,
        data: data,
        success: function (response) {
            result = response;
        }
    });
    return result;
}


var regexNum =  /^[0-9]*[1-9][0-9]*$/;
function isNumber(num) {
    return regexNum.test(num);
}

function isNull(str) {
    if (str == null || str.length == 0) {
        return true;
    } else {
        return false;
    }
}

function get(url,params) {
    var result= {};
    axios.post(url,Qs.stringify(params))
        .then(function (response) {
            console.log(response.data);
            result = response.data;
        })
        .catch(function (error) {
            console.log(error);
        });
    return result;
}