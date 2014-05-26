package pl.edu.agh.kaflog.hadoopconsumer;

import com.linkedin.camus.coders.CamusWrapper;
import com.linkedin.camus.etl.IEtlKey;
import com.linkedin.camus.etl.RecordWriterProvider;
import com.linkedin.camus.etl.kafka.mapred.EtlMultiOutputFormat;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.LogMessageSerializer;

import java.io.IOException;


public class LogMessageRecordWriterProvider implements RecordWriterProvider {
    public static final String ETL_OUTPUT_RECORD_DELIMITER = "etl.output.record.delimiter";
    public static final String DEFAULT_RECORD_DELIMITER    = "";

    protected String recordDelimiter = null;

    @Override
    public String getFilenameExtension() {
        return "";
    }

    @Override
    public RecordWriter<IEtlKey, CamusWrapper> getDataRecordWriter(TaskAttemptContext context, String fileName, CamusWrapper camusWrapper, FileOutputCommitter committer) throws IOException, InterruptedException {
        // If recordDelimiter hasn't been initialized, do so now
        if (recordDelimiter == null) {
            recordDelimiter = context.getConfiguration().get(
                    ETL_OUTPUT_RECORD_DELIMITER,
                    DEFAULT_RECORD_DELIMITER
            );
        }

        // Get the filename for this RecordWriter.
        Path path = new Path(
                committer.getWorkPath(),
                EtlMultiOutputFormat.getUniqueFile(
                        context, fileName, getFilenameExtension()
                )
        );

        // Create a FSDataOutputStream stream that will write to path.
        final FSDataOutputStream writer = path.getFileSystem(context.getConfiguration()).create(path);

        // Return a new anonymous RecordWriter that uses the
        // FSDataOutputStream writer to write bytes straight into path.
        return new RecordWriter<IEtlKey, CamusWrapper>() {
            @Override
            public void write(IEtlKey ignore, CamusWrapper data) throws IOException {
                LogMessage message = (LogMessage) data.getRecord();
                String record = message.toHdfsFormat() + recordDelimiter;
                writer.write(record.getBytes());
            }

            @Override
            public void close(TaskAttemptContext context) throws IOException, InterruptedException {
                writer.close();
            }
        };
    }
}
