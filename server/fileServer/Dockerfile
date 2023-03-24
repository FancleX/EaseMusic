FROM python:3.8

WORKDIR /app
ADD . /app
RUN pip3 install -r requirements.txt
RUN apt update && apt install -y ffmpeg
RUN python3 -m grpc_tools.protoc -I proto --python_out=. --grpc_python_out=. proto/DownloadService.proto

ENTRYPOINT ["python3", "server.py"]
