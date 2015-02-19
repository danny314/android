from google.appengine.api import users
import webapp2

from FWUtils import *
from FWLesson import FWLesson
from LessonOverview import LessonOverview

from google.appengine.api import search
from google.appengine.api.search import SortExpression
from google.appengine.api.search import SortOptions

class SearchLessonsService(webapp2.RequestHandler):

    def post(self):
        print "SearchLessonsService received body = " + self.request.body
        
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        queryString = jsonObj['queryString']
        searchResults = list()
        
        try:
            index = search.Index(INDEX_NAME)
            
            sortopts = SortOptions(expressions=[
            SortExpression(expression='votes', direction=SortExpression.DESCENDING, default_value=0),
            SortExpression(expression='lessonName', direction=SortExpression.ASCENDING)
            ],
            limit=1000)

            search_query = search.Query(
                query_string=queryString.strip(),
                options=search.QueryOptions(limit=20,sort_options=sortopts,))
            
            search_results = index.search(search_query)
            for doc in search_results:
                doc_id = doc.doc_id
                lessonName = doc.field('lessonName').value
                lessonDesc = doc.field('lessonDesc').value
                lessonWords = doc.field('lessonWords').value
                
                print 'doc id = ' + str(doc_id)
                print 'lessonName = ' + lessonName 
                print 'lessonDesc = ' + lessonDesc 
                print 'lessonWords = ' + lessonWords 
                 
                lessonOverview = LessonOverview(lessonName,lessonDesc,None,None,None,None)
                searchResults.append(lessonOverview)
                
        except search.Error:
            print 'Error conducting index search'
        
        jsonResponse = to_JSON(searchResults)
        print jsonResponse
        
        self.response.set_status(200)
        self.response.headers['Content-Type'] = 'application/json'
        
        self.response.out.write(jsonResponse)

