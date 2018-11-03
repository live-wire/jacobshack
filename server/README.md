# Server setup :bomb:

---
#### Setup
- Activate virtualenvironment inside the jacobshack directory
- `$ pip install numpy Cython`
- cd out of the jacobshack directory
- `$ git clone https://github.com/pdollar/coco.git`
- `$ cd coco/PythonAPI`
- `$ make`
- `$ python setup.py build_ext install`
- cd jacobshack/server
- `$ pip install -r requirements.txt`
- `$ python server.py`
