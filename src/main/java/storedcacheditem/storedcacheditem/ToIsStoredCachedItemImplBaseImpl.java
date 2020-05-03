package storedcacheditem.storedcacheditem;

import com.google.protobuf.ByteString;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import id.id.IsId;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import storedcacheditem.input.IsInput;
import storedcacheditem.output.IsOutput;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToIsStoredCachedItemImplBaseImpl extends ToIsStoredCachedItemGrpc.ToIsStoredCachedItemImplBase {

    private final Logger logger;
    private final HazelcastInstance hzInstance;
    private final Map<String, ByteString> general;
    private final ILock lock;

    public ToIsStoredCachedItemImplBaseImpl() {
        logger = Logger.getLogger(getClass().getName());
        logger.info("starting");

        logger.info("starting hazelcast");
        hzInstance = Hazelcast.newHazelcastInstance();
        logger.info("started hazelcast");

        logger.info("starting general map: hazelcast");
        general = hzInstance.getMap("general");
        logger.info("started general map: hazelcast");

        logger.info("starting general lock: hazelcast");
        lock = hzInstance.getLock("general");
        logger.info("started general lock: hazelcast");

        logger.info("started");
    }

    @Override
    public void produce(final NotStoredCachedItem request, final StreamObserver<IsStoredCachedItem> responseObserver) {
        try {
            final IsId isId;
            final String isStringValue;

            if (!request.hasIsInput()) {
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
            }
            final IsInput isInput = request.getIsInput();
            if (!isInput.hasIsId()) {
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
            } else {
                isId = isInput.getIsId();
                if (!isId.hasIsOutput()) {
                    throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
                }
            }
            if (isInput.getIsItemBytes() == null) {
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
            }
            if (isInput.getIsItemBytes()
                    .isEmpty()) {
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
            }

            isStringValue = isId.getIsOutput()
                    .getIsStringValue();

            if (isStringValue.isEmpty()) {
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
            }

            if (isInput.getIsUseLockBoolean()) {
                lock.lock();
                try {

                    if (general.get(isStringValue) != null) {
                        throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("409"));
                    }
                    general.put(isStringValue,
                            isInput.getIsItemBytes());
                } finally {
                    lock.unlock();
                }
            } else {
                general.put(isStringValue,
                        isInput.getIsItemBytes());
            }

            responseObserver.onNext(IsStoredCachedItem.newBuilder()
                    .setIsOutput(IsOutput.newBuilder()
                            .build())
                    .build());
            responseObserver.onCompleted();
        } catch (final Exception e) {
            logger.log(Level.SEVERE,
                    e.getMessage(),
                    e);
            responseObserver.onError(e);
        }
    }
}
