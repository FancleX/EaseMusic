# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: DownloadService.proto
"""Generated protocol buffer code."""
from google.protobuf.internal import builder as _builder
from google.protobuf import descriptor as _descriptor
from google.protobuf import descriptor_pool as _descriptor_pool
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor_pool.Default().AddSerializedFile(b'\n\x15\x44ownloadService.proto\x12\x0c\x63om.neu.grpc\"\x1f\n\x0f\x44ownloadRequest\x12\x0c\n\x04uuid\x18\x01 \x01(\t\"2\n\x10\x44ownloadResponse\x12\x0c\n\x04\x66ile\x18\x01 \x01(\x0c\x12\x10\n\x08\x66ilePath\x18\x02 \x01(\t\"?\n\x0fReadFileRequest\x12\x10\n\x08\x66ilePath\x18\x01 \x01(\t\x12\r\n\x05start\x18\x02 \x01(\x04\x12\x0b\n\x03\x65nd\x18\x03 \x01(\x04\" \n\x10ReadFileResponse\x12\x0c\n\x04\x66ile\x18\x01 \x01(\x0c\")\n\x15\x44\x65leteResourceRequest\x12\x10\n\x08\x66ilePath\x18\x01 \x01(\t\"\x18\n\x16\x44\x65leteResourceResponse2\xfc\x01\n\x0f\x44ownloadService\x12K\n\x08\x64ownload\x12\x1d.com.neu.grpc.DownloadRequest\x1a\x1e.com.neu.grpc.DownloadResponse0\x01\x12G\n\x04read\x12\x1d.com.neu.grpc.ReadFileRequest\x1a\x1e.com.neu.grpc.ReadFileResponse0\x01\x12S\n\x06\x64\x65lete\x12#.com.neu.grpc.DeleteResourceRequest\x1a$.com.neu.grpc.DeleteResourceResponseB\x02P\x01\x62\x06proto3')

_builder.BuildMessageAndEnumDescriptors(DESCRIPTOR, globals())
_builder.BuildTopDescriptorsAndMessages(DESCRIPTOR, 'DownloadService_pb2', globals())
if _descriptor._USE_C_DESCRIPTORS == False:

  DESCRIPTOR._options = None
  DESCRIPTOR._serialized_options = b'P\001'
  _DOWNLOADREQUEST._serialized_start=39
  _DOWNLOADREQUEST._serialized_end=70
  _DOWNLOADRESPONSE._serialized_start=72
  _DOWNLOADRESPONSE._serialized_end=122
  _READFILEREQUEST._serialized_start=124
  _READFILEREQUEST._serialized_end=187
  _READFILERESPONSE._serialized_start=189
  _READFILERESPONSE._serialized_end=221
  _DELETERESOURCEREQUEST._serialized_start=223
  _DELETERESOURCEREQUEST._serialized_end=264
  _DELETERESOURCERESPONSE._serialized_start=266
  _DELETERESOURCERESPONSE._serialized_end=290
  _DOWNLOADSERVICE._serialized_start=293
  _DOWNLOADSERVICE._serialized_end=545
# @@protoc_insertion_point(module_scope)