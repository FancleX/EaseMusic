protoc: proto/DownloadService.proto
	python3 -m grpc_tools.protoc -I proto --python_out=. --grpc_python_out=. proto/DownloadService.proto

build: 
	docker build -t file-server .

clean:
	docker image rm file-server

run:
	docker run --publish 9001:9001 file-server