import DownloadService_pb2, DownloadService_pb2_grpc
import logging
import yt_dlp
import os

class Service(DownloadService_pb2_grpc.DownloadServiceServicer):

    file_path_dir = "./audios"
    request_url_prefix = "https://www.youtube.com/watch?v="

    def __init__(self) -> None:
        logging.basicConfig(level = logging.INFO)
        
    
    def download(self, request: DownloadService_pb2.DownloadRequest, context):
        video_id = request.uuid
        audio_name = f'{video_id}.mp3'
        audio_path_without_extension = f'{self.file_path_dir}/{video_id}'
        audio_path_with_extension = f'{self.file_path_dir}/{audio_name}'
        
        if not os.path.isfile(audio_path_with_extension):
            logging.info(f"request to donwload: {video_id}")

            ydl_opts = {
                    'format': 'm4a/bestaudio/best',
                    # ℹ️ See help(yt_dlp.postprocessor) for a list of available Postprocessors and their arguments
                    'postprocessors': [{  # Extract audio using ffmpeg
                        'key': 'FFmpegExtractAudio',
                        'preferredcodec': 'mp3',
                        'preferredquality': 192
                    }],
                    'outtmpl': audio_path_without_extension
                }
            

            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                ydl.download(self.request_url_prefix + video_id)
        
        logging.info(f"finished downloading: {audio_name}, start streaming")
        with open(audio_path_with_extension, mode='rb') as audio:
            while chunk := audio.read(1024):
                yield DownloadService_pb2.DownloadResponse(file=chunk, filePath=audio_path_with_extension)



    def read(self, request, context):
        path = request.filePath
        startIndex = request.start
        currentIndex = startIndex
        endIndex = request.end

        logging.info(f'start reading file from the path: {path} from {startIndex} to {endIndex}')
        with open(path, mode='rb') as file:
            file.seek(startIndex)
            read_buffer = 1024 if endIndex - startIndex > 1024 else endIndex - startIndex

            while (chunk := file.read(read_buffer)) and currentIndex < endIndex:
                currentIndex += len(chunk)
                read_buffer = 1024 if endIndex - currentIndex > 1024 else endIndex - currentIndex
                # logging.info(f'read bytes: {currentIndex}')
                yield DownloadService_pb2.ReadFileResponse(file=chunk)
                            
    def delete(self, request, context):
        path = request.filePath

        logging.info(f'start deletling file at {path}')

        if os.path.isfile(path):
            os.remove(path)
