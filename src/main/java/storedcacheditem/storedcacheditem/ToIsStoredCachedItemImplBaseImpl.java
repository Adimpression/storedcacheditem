package storedcacheditem.storedcacheditem;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class ToIsStoredCachedItemImplBaseImpl extends ToIsStoredCachedItemGrpc.ToIsStoredCachedItemImplBase {

    private final Logger logger;

    public ToIsStoredCachedItemImplBaseImpl() {
        logger = Logger.getLogger(getClass().getName());
        logger.info("starting");

        logger.info("started");
    }

    @Override
    public void produce(final NotStoredCachedItem request, final StreamObserver<IsStoredCachedItem> responseObserver) {
        try {
            if (!request.hasIsInput()) {
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
            }
            responseObserver.onNext(IsStoredCachedItem.newBuilder()
                    .build());
            responseObserver.onCompleted();
        } catch (final Exception e) {
            responseObserver.onError(e);
        }
    }
}
