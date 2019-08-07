const ApplicationType = Java.type('org.kie.kogito.examples.Application');
const ApplicantType = Java.type('org.kie.kogito.examples.polyglot.Applicant');
const ApplicantProcessModelType = Java.type('org.kogito.examples.polyglot.ApplicantprocessModel');
const HashMapType = Java.type('java.util.HashMap');
const http = require('http');
const querystring = require('querystring');

const server = http.createServer((req, res) => {
    if (req.method === 'POST') {
        console.log('Received Applicant info');
        var body = '';
        req.on('data', function (data) {
            body += data;
            //console.log('Partial body: ' + body);
        });
        req.on('end', function () {
            console.log('Body: ' + body);
            var queryData = querystring.parse(body);

            var applicant = new ApplicantType();
            applicant.setFname(queryData.fname);
            applicant.setLname(queryData.lname);
            applicant.setAge(queryData.age);

            getApplicantProcessInstance(applicant).start();

            res.writeHead(200, {
                'Content-Type': 'text/html'
            });

            if (applicant.isValid()) {
                res.end('Applicant validation was successful.');
            } else {
                res.end('Applicant failed validation.');
            }
        });
    } else {
        res.end(`
            <!doctype html>
            <html>
            <body>
                <form action="/" method="post">
                <fieldset>
                    <legend>Enter Applicant Info</legend>
                    <p><input type="text" id="fname" name="fname" />
                    <label for="fname">First Name</label></p>
                    <p><input type="text" id="lname" name="lname" />
                    <label for="lname">Last Name</label></p>
                    <p><input type="number" id="age" name="age" />
                    <label for="age">Age</label></p>
                    <p><button>Evaluate</button></p>
                </fieldset>
                </form>
            </body>
            </html>
        `);
    }
});
server.listen(8080, function () {
    console.log("jBPM Applicant Test server running at http://127.0.0.1:8080/");
});

function getApplicantProcessInstance(applicant) {

    var kogitoApp = new ApplicationType();

    var model = new ApplicantProcessModelType();
    model.setApplicant(applicant);

    var process = kogitoApp.processes().createApplicantprocessProcess();
    return process.createInstance(model);

}