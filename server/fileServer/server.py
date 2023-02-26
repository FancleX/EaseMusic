from concurrent import futures
import grpc

server_port = 9001


def main():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    server.add_insecure_port("[::]:" + server_port)
    server.start()
    print("Server started at " + server_port)
    server.wait_for_termination()

if __name__ == '__main__':
    main()