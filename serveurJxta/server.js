var express = require('express');
var app = express();
var fs = require('fs');

var ipsFile = "ips.txt";

app.get('/request/:ip', function (req, res) {
    console.log("request");
    var data = ""
    var ip = req.params.ip;
    data = readFile(ipsFile);
    if(validateIPaddress(ip)){
        var datas = data.split(";");
        if(!ipExist(datas, ip)){
            saveInfile(ipsFile, ip);
        }
    }
    res.send(data);
});

app.listen(3000, function () {
    console.log('listening on port 3000!');
});



function validateIPaddress(ip)
{
    if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(ip))
    {
        return (true)
    }
    return (false)
}

function saveInfile(file, ip){
    fs.appendFileSync(file, ip + ";", encoding='utf8');
}

function readFile(file){
    return fs.readFileSync(file, 'utf8', function (err,data) {
        if (err) {
            console.log("erreur : "+err);
        }
    });
}

function ipExist(datas, ip){
    for(var i = 0; i<datas.length; i++){
        if(ip == datas[i]){
            return true;
        }
    }
    return false;
}