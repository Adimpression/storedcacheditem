package storedcacheditem.storedcacheditem;

import com.google.protobuf.ByteString;
import id.id.IsId;
import id.output.IsOutput;
import io.grpc.Deadline;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import main.Test;
import storedcacheditem.input.IsInput;

import java.util.concurrent.TimeUnit;

public class ToIsStoredCachedItemImplBaseImplTest extends Test {

    @org.testng.annotations.Test(expectedExceptions = StatusRuntimeException.class, expectedExceptionsMessageRegExp = "INVALID_ARGUMENT: 422")
    public void testEmpty() {
        final ToIsStoredCachedItemGrpc.ToIsStoredCachedItemBlockingStub toIsStoredCachedItemBlockingStub = ToIsStoredCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsStoredCachedItemGrpc.SERVICE_NAME)
                .usePlaintext()
                .build())
                .withDeadline(Deadline.after(30,
                        TimeUnit.SECONDS))
                .withWaitForReady();

        final IsStoredCachedItem ignored = toIsStoredCachedItemBlockingStub.produce(NotStoredCachedItem.newBuilder()
                .build());

    }

    @org.testng.annotations.Test(expectedExceptions = StatusRuntimeException.class, expectedExceptionsMessageRegExp = "INVALID_ARGUMENT: 422")
    public void testStoreEmptyBytes() {
        final ToIsStoredCachedItemGrpc.ToIsStoredCachedItemBlockingStub toIsStoredCachedItemBlockingStub = ToIsStoredCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsStoredCachedItemGrpc.SERVICE_NAME)
                .usePlaintext()
                .build())
                .withDeadline(Deadline.after(30,
                        TimeUnit.SECONDS))
                .withWaitForReady();

        final IsStoredCachedItem isStoredCachedItem = toIsStoredCachedItemBlockingStub.produce(NotStoredCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(String.valueOf(System.currentTimeMillis()))
                                        .build())
                                .build())
                        .setIsItemBytes(ByteString.copyFromUtf8(""))
                        .setIsUseLockBoolean(true)
                        .build())
                .build());

        assert isStoredCachedItem.hasIsOutput();

    }

    @org.testng.annotations.Test()
    public void testStoreAccepted() {
        final ToIsStoredCachedItemGrpc.ToIsStoredCachedItemBlockingStub toIsStoredCachedItemBlockingStub = ToIsStoredCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsStoredCachedItemGrpc.SERVICE_NAME)
                .usePlaintext()
                .build())
                .withDeadline(Deadline.after(30,
                        TimeUnit.SECONDS))
                .withWaitForReady();

        final IsStoredCachedItem isStoredCachedItem = toIsStoredCachedItemBlockingStub.produce(NotStoredCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(String.valueOf(System.currentTimeMillis()))
                                        .build())
                                .build())
                        .setIsItemBytes(ByteString.copyFromUtf8("Hello " + System.currentTimeMillis()))
                        .setIsUseLockBoolean(true)
                        .build())
                .build());

        assert isStoredCachedItem.hasIsOutput();

    }
}