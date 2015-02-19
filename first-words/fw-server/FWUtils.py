import jinja2
import json
import os
import urllib
import datetime
import time

from google.appengine.ext import ndb
from google.appengine.api import users

from django.utils import simplejson

JINJA_ENVIRONMENT = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__)),
    extensions=['jinja2.ext.autoescape'],
    autoescape=True)

HOST = 'mythic-code-689.appspot.com'
BASE_URL = 'http://mythic-code-689.appspot.com/'

#HOST = 'localhost:8080'
#BASE_URL = 'http://localhost:8080/'

FROM_EMAIL = 'danny.explorer@gmail.com'
TO_EMAIL = ['danny.explorer@gmail.com']

INVITATION_SUBJECT = 'Please subscribe to my new stream '

SIMPLE_TYPES = (int, long, float, bool, dict, basestring, list)

INDEX_NAME = 'fw-lessonIndex2'

LESSON_TYPE_ALPHA = 'ALPHA'
LESSON_TYPE_NUMBERS = 'NUMBERS'
LESSON_TYPE_COLORS = 'COLORS'

LESSON_OWNERSHIP_PUBLIC = 'PUBLIC'
LESSON_OWNERSHIP_SELF = 'SELF'
LESSON_OWNERSHIP_SYSTEM = 'SYSTEM'

SYSTEM_USER = 'fw_admin'

def getParentKey():
    return ndb.Key('FirstWords', 'mythic-code-689')

def to_JSON(self):
    return json.dumps(self, default=lambda o: o.__dict__, indent=4)    

def is_json(myjson):
    try:
        json_object = json.loads(myjson)
    except ValueError, e:
            return False
    return True

def getJsonObjFromFormData(formData):
    try:
        jsonObj = json.loads(formData)
    except ValueError, e:
        jsonStr = urllib.unquote(formData('data')).decode('utf8') 
        jsonObj = json.loads(jsonStr)
        
    return jsonObj

def send_request(conn, url, req):
    print ' sending request to ' + url
    print '%s' % json.dumps(req)
    conn.request("POST", url, json.dumps(req), {"Content-type": "application/json", "Accept": "text/plain"})
    resp = conn.getresponse()
    
    responseStr = resp.read()
    print 'ConnexusUtils = ' + responseStr
    jsonresp = ''
    if len(responseStr) > 0:
        jsonresp = json.loads(responseStr)
        print 'ConnexusUtils =   %s' % jsonresp
    else:
        print 'WARN:No body in response'
            
    return jsonresp

def getEpochTime():
    currentDate = datetime.datetime.now()
    return int(time.mktime(currentDate.timetuple()))

def addJsonProperty(str,key, value):
    if (str == ''):
        str = '{'
    else: 
        pass
    str = str + '"' + key + '":' + value + '"'
    return str

"""

def to_dict(model):
    output = {}

    for key, prop in model.properties().iteritems():
        value = getattr(model, key)

        if value is None or isinstance(value, SIMPLE_TYPES):
            output[key] = value
        elif isinstance(value, datetime.date):
            # Convert date/datetime to MILLISECONDS-since-epoch (JS "new Date()").
            ms = time.mktime(value.utctimetuple()) * 1000
            ms += getattr(value, 'microseconds', 0) / 1000
            output[key] = int(ms)
        elif isinstance(value, ndb.GeoPt):
            output[key] = {'lat': value.lat, 'lon': value.lon}
        elif isinstance(value, ndb.Model):
            output[key] = to_dict(value)
        else:
            raise ValueError('cannot encode ' + repr(prop))

    return output

"""
class JsonEncoder(simplejson.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime.datetime):
            return obj.strftime("%m/%d/%Y %H:%M:%S")

        elif isinstance(obj, ndb.Model):
            return obj.to_dict()
            #return dict((p, getattr(obj, p)) 
            #            for p in obj.properties())

        elif isinstance(obj, users.User):
            return obj.email()

        else:
            return simplejson.JSONEncoder.default(self, obj)
        
        

"""
# use the encoder as: 
simplejson.dumps(model, cls=jsonEncoder)        
class DictModel(ndb.Model):
    def to_dict(self):
       return dict([(p, unicode(getattr(self, p))) for p in self.properties()])        
"""    
