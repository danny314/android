import webapp2

from FWUser import FWUser
from FWUtils import getJsonObjFromFormData

class LoginUserService(webapp2.RequestHandler):

    def post(self):
        print 'Login User Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)
        username = jsonObj['username']
        password = jsonObj['password']
        
        savedUser = FWUser.query(FWUser.username==username,FWUser.password==password).fetch()
        if len(savedUser) > 0:
            print 'Found user ' + username
            jsonResponse = '{"status":"OK"}'
            self.response.set_status(200)
        else:
            self.response.set_status(400)
            print 'Authentication failed for user ' + username
            jsonResponse = '{"status":"FAILED","error":"AUTH_FAILED"}'
            
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(jsonResponse)
            
