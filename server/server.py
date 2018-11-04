from flask import Flask,request
from waitress import serve
from algoliasearch import algoliasearch
import base64
from PIL import Image
import random
import json
from sample import getCaption
from build_vocab import Vocabulary
import os


app = Flask(__name__)
client = algoliasearch.Client(os.environ['ALGOLIA_APPLICATION'], os.environ['ALGOLIA_ADMIN'])
index = client.init_index('whodat')
index.set_settings({
  'searchableAttributes': [
    'caption'
  ]
})

@app.route('/classify', methods=['GET', 'POST'])
def putResultsInAlgolia():
    content = request.get_json(silent=True)

    # Got image encoded in base 64, need to convert it to png
    blah = content['base64image']
    blah = blah.replace("data:image/png;base64,", "")
    blah = blah.replace(" ", "+")
    if content:
        # converting base64 image to png
        with open("imageToSave.png", "wb") as fh:
            fh.write(base64.decodebytes(bytes(blah, 'utf-8')))
        im = Image.open("imageToSave.png")
        s = str(random.randint(1, 100000)) + ".png"
        im.save("collection/" + s)

        #rgb_im = im.convert('RGB')
        #rgb_im.save('imageToSave.jpg')

        result = getCaption("collection/" + s)
        if result != None:
            index.addObject(result)
            print("PREDICTION:", result)
            return json.dumps({'status': 'OK', 'caption': result['caption']})
        return json.dumps({"status": "ERROR"})
    else:
        return json.dumps({"status": "ERROR"})


@app.route('/getCaptions', methods=['GET'])
def getResultsFromAlgolia():
    content = request.args.get('query')
    result = index.search(content,
    {"attributesToRetrieve": "url,caption",'restrictSearchableAttributes': [
    'caption'
     ]})
    return json.dumps(result)


if __name__ == '__main__':
    serve(app,host='0.0.0.0', port=5001)
    app.run()

