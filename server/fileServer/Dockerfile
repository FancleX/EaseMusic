FROM python:3.11

COPY ./requirements.txt /requirements.txt
RUN pip3 install -r /requirements.txt
# RUN python3 -m grpc_tools.protoc -I. --python_out=./grpc_compiled --grpc_python_out=./grpc_compiled DownloadService.proto
RUN apt update && apt install -y ffmpeg
RUN python3 -m grpc_tools.protoc -I proto --python_out=. --grpc_python_out=. proto/DownloadService.proto

ENTRYPOINT ["python3", "server.py"]