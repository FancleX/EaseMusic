from concurrent import futures
import grpc
import DownloadServiceImpl
import DownloadService_pb2_grpc

def main():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    DownloadService_pb2_grpc.add_DownloadServiceServicer_to_server(DownloadServiceImpl.Service(), server)
    server.add_insecure_port("0.0.0.0:9001")
    server.start()
    print("Server started at 9001")
    server.wait_for_termination()

if __name__ == '__main__':
    main()