package soya.framework.transform.schema.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AvroUtils {

    private AvroUtils() {

    }

    public static void writeAsJson(GenericRecord record,
                                   Schema schema,
                                   OutputStream outputStream) throws IOException {

        GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        Encoder encoder = EncoderFactory.get().jsonEncoder(schema, outputStream);

        writer.write(record, encoder);
        encoder.flush();
        outputStream.close();
    }

    public static void writeAsBinary(GenericRecord record,
                                     Schema schema,
                                     OutputStream outputStream) throws IOException {

        GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

        writer.write(record, encoder);
        encoder.flush();
        outputStream.close();
    }

    public static void write(GenericRecord record, Schema schema, File avro) throws Exception {
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(new GenericDatumWriter<>(schema));
        FileOutputStream outputStream = new FileOutputStream(avro);
        dataFileWriter.create(schema, outputStream);

        dataFileWriter.append(record);
        dataFileWriter.close();

    }

    private static void write(List<GenericRecord> records, Schema schema, File avro) throws Exception {
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(new GenericDatumWriter<>(schema));
        FileOutputStream outputStream = new FileOutputStream(avro);
        dataFileWriter.create(schema, outputStream);
        records.forEach(e -> {
            try {
                dataFileWriter.append(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        dataFileWriter.close();

    }

    private static List<GenericRecord> read(File avro) throws Exception {
        List<GenericRecord> records = new ArrayList<>();

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        DataFileReader<GenericRecord> dataFileReader =
                new DataFileReader<GenericRecord>(avro, datumReader);

        dataFileReader.forEach(e -> {
            records.add(e);
        });

        return records;
    }

    public static GenericRecord read(byte[] data, Schema schema) throws Exception {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        datumReader.setSchema(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        return datumReader.read(null, decoder);
    }

}

