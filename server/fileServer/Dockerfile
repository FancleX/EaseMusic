FROM python:3.11

COPY ./requirements.txt /requirements.txt
RUN pip3 install -r /requirements.txt
RUN python3 -m grpc_tools.protoc -I. --python_out=./grpc_compiled --grpc_python_out=./grpc_compiled DownloadService.proto

ENTRYPOINT ["python3", "server.py"]