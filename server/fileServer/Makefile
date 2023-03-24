protoc: proto/DownloadService.proto
	python3 -m grpc_tools.protoc -I proto --python_out=. --grpc_python_out=. proto/DownloadService.proto

build:
	docker build -t file-server .

clean:
	docker image rm file-server

run:
	docker run -v /media/data10g/file-server-app:/app/data -p 9001:9001 -it --restart=unless-stopped -d file-server
