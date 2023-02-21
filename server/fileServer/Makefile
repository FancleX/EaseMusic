build: 
	docker build -t file-server .

clean:
	docker image rm file-server

run:
	docker run --publish 9001:9001 file-server