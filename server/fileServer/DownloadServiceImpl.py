from . import grpc_compiled

import grpc_compiled.DownloadService_pb2
import grpc_compiled.DownloadService_pb2_grpc

class Service(grpc_compiled.DownloadService_pb2_grpc.DownloadService):
    
    def download(self, request, target):
        
        pass

    def read(self, request, target):
        pass

    def delete(self, request, target):
        pass
