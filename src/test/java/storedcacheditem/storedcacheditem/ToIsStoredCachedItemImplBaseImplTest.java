package storedcacheditem.storedcacheditem;

import io.grpc.Deadline;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import main.Test;

import java.util.concurrent.TimeUnit;

public class ToIsStoredCachedItemImplBaseImplTest extends Test {

    @org.testng.annotations.Test(expectedExceptions = StatusRuntimeException.class, expectedExceptionsMessageRegExp = "INVALID_ARGUMENT: 422")
    public void testEmpty() {
        final ToIsStoredCachedItemGrpc.ToIsStoredCachedItemBlockingStub toIsStoredCachedItemBlockingStub = ToIsStoredCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsStoredCachedItemGrpc.SERVICE_NAME)
                .usePlaintext()
                .build())
                .withDeadline(Deadline.after(5,
                        TimeUnit.SECONDS))
                .withWaitForReady();

        final IsStoredCachedItem ignored = toIsStoredCachedItemBlockingStub.produce(NotStoredCachedItem.newBuilder()
                .build());

    }
}