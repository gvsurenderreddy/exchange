/*
 * This file is part of Bitsquare.
 *
 * Bitsquare is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bitsquare is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bitsquare. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bitsquare.trade.protocol.trade.offerer.tasks;

import io.bitsquare.trade.listeners.SendMessageListener;
import io.bitsquare.trade.protocol.trade.offerer.BuyerAsOffererModel;
import io.bitsquare.trade.protocol.trade.offerer.messages.RequestDepositPaymentMessage;
import io.bitsquare.util.taskrunner.Task;
import io.bitsquare.util.taskrunner.TaskRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDepositPayment extends Task<BuyerAsOffererModel> {
    private static final Logger log = LoggerFactory.getLogger(RequestDepositPayment.class);

    public RequestDepositPayment(TaskRunner taskHandler, BuyerAsOffererModel model) {
        super(taskHandler, model);
    }

    @Override
    protected void doRun() {
        model.getOffererPubKey();
        RequestDepositPaymentMessage tradeMessage = new RequestDepositPaymentMessage(
                model.getId(),
                model.getOffererConnectedOutputsForAllInputs(),
                model.getOffererOutputs(),
                model.getOffererPubKey(),
                model.getBankAccount(),
                model.getAccountId());

        model.getTradeMessageService().sendMessage(model.getTaker(), tradeMessage, new SendMessageListener() {
            @Override
            public void handleResult() {
                log.trace("RequestTakerDepositPaymentMessage successfully arrived at peer");
                complete();
            }

            @Override
            public void handleFault() {
                failed("RequestTakerDepositPaymentMessage did not arrive at peer");
            }
        });
    }

    @Override
    protected void updateStateOnFault() {
    }
}
